package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.*;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.refreshtoken.dto.RefreshTokenRequest;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.refreshtoken.service.RefreshTokenService;
import com.kit.wmsbackend.feature.user.dto.UserResetPasswordEvent;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import com.kit.wmsbackend.security.TokenHashingService;
import com.kit.wmsbackend.utils.SecurityUtils;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
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
    RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthLoginResponse login(@NonNull AuthLoginRequest authLoginRequest) {
        String normalizedEmail = normalizeEmail(authLoginRequest.email());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                authLoginRequest.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String sessionId = UUID.randomUUID().toString();
        String jti = UUID.randomUUID().toString();

        String accessToken = jwtService.buildAccessToken(normalizedEmail, sessionId);
        String refreshToken = jwtService.buildRefreshToken(normalizedEmail, sessionId, jti);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if (userPrincipal != null) {
            User user = userRepository.getReferenceById(userPrincipal.getId());
            refreshTokenService.create(
                    new RefreshTokenRequest(
                            user,
                            jti,
                            sessionId,
                            refreshToken
                    )
            );
        }

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
        String jti = jwtService.extractJti(jwt);
        String sessionId = jwtService.extractSessionId(jwt);

        if (userEmail == null  || jti == null || sessionId == null) {
            throw new JwtException("Invalid refresh token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        RefreshToken currentRefreshToken = refreshTokenRepository
                .findValidByUserIdAndJtiForUpdate(userPrincipal.getId(), jti, Instant.now())
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        if (jwtService.isTokenValid(jwt, userDetails) &&
                jwtService.matchesStoredRefreshToken(jwt, currentRefreshToken)) {
            if (!userDetails.isEnabled()) {
                throw new AppException(ErrorCode.AUTH_INVALID_ACCOUNT);
            }

            refreshTokenRepository.delete(currentRefreshToken);

            String newJti = UUID.randomUUID().toString();
            String accessToken = jwtService.buildAccessToken(userEmail, sessionId);
            String refreshToken = jwtService.buildRefreshToken(
                    userEmail,
                    sessionId,
                    newJti,
                    refreshSessionExpiresAt
            );

            User user = userRepository.getReferenceById(userPrincipal.getId());

            refreshTokenService.create(
                    new RefreshTokenRequest(
                            user,
                            newJti,
                            sessionId,
                            refreshToken
                    )
            );

            return new AuthRefreshTokenResponse(accessToken, refreshToken);
        }

        throw new JwtException("Invalid refresh token");
    }

    @Override
    @Transactional
    public void forgotPassword(@NonNull AuthForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) return;

        String resetToken = jwtService.buildResetToken(user.getEmail());

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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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
}

