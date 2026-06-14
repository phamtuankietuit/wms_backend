package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.*;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.user.dto.UserResetPasswordEvent;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.utils.SecurityUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager;
    JwtService jwtService;
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    MediaAssetRepository mediaAssetRepository;
    UserDetailsService userDetailsService;
    PasswordEncoder passwordEncoder;
    AuthMapper authMapper;
    TokenHashingService tokenHashingService;
    ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AuthLoginResponse login(
        @NonNull AuthLoginRequest authLoginRequest,
        HttpServletRequest request
    ) {
        String normalizedEmail = normalizeEmail(authLoginRequest.email());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                authLoginRequest.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = getAuthenticatedUser(authentication);
        validateTokenEligibleUser(user);

        String sessionId = UUID.randomUUID().toString();
        String jti = UUID.randomUUID().toString();

        String accessToken = jwtService.createAccessToken(user, sessionId);
        String refreshToken = jwtService.createRefreshToken(user, sessionId, jti);

        return new AuthLoginResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthRefreshTokenResponse refreshToken(String bearerToken) {
        String jwt = jwtService.resolveBearerToken(bearerToken);

        if (jwt == null || !jwtService.isTokenType(jwt, TokenType.REFRESH_TOKEN)) {
            throw new JwtException("Invalid refresh token");
        }

        Instant refreshSessionExpiresAt = jwtService.extractRefreshSessionExpiresAt(jwt);

        if (!refreshSessionExpiresAt.isAfter(Instant.now())) {
            throw new JwtException("Invalid refresh token");
        }

        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail == null) {
            throw new JwtException("Invalid refresh token");
        }

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        String jti = jwtService.extractJti(jwt);

        if (jti == null) {
            throw new JwtException("Invalid refresh token");
        }

        RefreshToken currentRefreshToken = refreshTokenRepository
                .findValidByUserIdAndJtiForUpdate(user.getId(), jti, Instant.now())
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        String sessionId = jwtService.extractSessionId(jwt);

        if (sessionId == null) {
            throw new JwtException("Invalid refresh token");
        }

        if (jwtService.isTokenValid(jwt, userDetails) &&
                jwtService.matchesStoredRefreshToken(jwt, currentRefreshToken)) {

            validateTokenEligibleUser(user);

            refreshTokenRepository.delete(currentRefreshToken);

            String newJti = UUID.randomUUID().toString();
            String accessToken = jwtService.createAccessToken(user, sessionId);
            String refreshToken = jwtService.createRefreshToken(
                    user,
                    sessionId,
                    newJti,
                    refreshSessionExpiresAt
            );

            return new AuthRefreshTokenResponse(accessToken, refreshToken);
        } else {
            throw new JwtException("Invalid refresh token");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(@NonNull AuthForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) return;

        String resetToken = jwtService.createResetToken(user.getEmail());

        user.setResetToken(tokenHashingService.hashToken(resetToken));

        eventPublisher.publishEvent(new UserResetPasswordEvent(
                user.getEmail(),
                user.getName(),
                resetToken
        ));
    }

    @Override
    @Transactional
    public void resetPassword(@NonNull AuthResetPasswordRequest request) {
        String email = jwtService.extractUsername(request.resetToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenType(request.resetToken(), TokenType.RESET_TOKEN) ||
                !jwtService.isTokenValid(request.resetToken(), userDetails)) {
            throw new JwtException("Invalid token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!jwtService.matchesResetStoredToken(user, request.resetToken())) {
            throw new JwtException("Invalid token");
        }

        user.setResetToken(null);
        user.setPassword(passwordEncoder.encode(request.password()));

        if (user.getStatus() == UserStatus.PENDING) {
            user.getStatus().validateTransitionTo(UserStatus.ACTIVE);
            user.setStatus(UserStatus.ACTIVE);
        }
    }

    @Override
    public AuthGetMeResponse getMe() {
        UserPrincipal userPrincipal = SecurityUtils.getCurrentUser();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, userPrincipal.getId().toString()));

        String avatar = mediaAssetRepository
                .findActiveByOwner(MediaOwnerType.USER, user.getId(), MediaResourceType.IMAGE)
                .stream()
                .findFirst()
                .map(MediaAsset::getSecureUrl)
                .orElse(null);

        return authMapper.toAuthGetMeResponse(user, avatar);
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private @NonNull User getAuthenticatedUser(@NonNull Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authenticated user no longer exists"));
        }

        throw new AuthenticationCredentialsNotFoundException("Authenticated principal is missing");
    }

    private void validateTokenEligibleUser(@NonNull User user) {
        if (user.isDeleted() || user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User account is not active");
        }
    }
}

