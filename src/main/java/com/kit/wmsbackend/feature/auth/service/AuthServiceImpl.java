package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.config.properties.ClientProperties;
import com.kit.wmsbackend.config.properties.JwtProperties;
import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.*;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import com.kit.wmsbackend.utils.SecurityUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
    MailService mailService;
    JwtProperties jwtProperties;
    ClientProperties clientProperties;

    @Override
    @Transactional
    public AuthLoginResponse login(
        @NonNull AuthLoginRequest authLoginRequest,
        HttpServletRequest request
    ) {
        String normalizedEmail = normalizeEmail(authLoginRequest.email());

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                authLoginRequest.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = getAuthenticatedUser(authentication);
        validateTokenEligibleUser(user);

        String jti = UUID.randomUUID().toString();

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user, jti, request);

        return new AuthLoginResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthRefreshTokenResponse refreshToken(@NonNull HttpServletRequest request) {
        String jwt = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(TokenType.REFRESH_TOKEN.toString())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null) {
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

        if (jwtService.isTokenValid(jwt, userDetails) &&
                jwtService.matchesStoredToken(user, jwt, TokenType.REFRESH_TOKEN, jti)) {
            validateTokenEligibleUser(user);

            Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            try {
                SecurityContextHolder.getContext().setAuthentication(authToken);

                RefreshToken refreshToken = refreshTokenRepository.findNotDeletedByJti(jti).orElse(null);

                if (refreshToken != null) {
                    refreshToken.setLastUsedAt(LocalDateTime.now());
                    refreshTokenRepository.save(refreshToken);
                }

                String accessToken = jwtService.createAccessToken(user);

                return new AuthRefreshTokenResponse(accessToken);
            } finally {
                SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
            }
        } else {
            throw new JwtException("Invalid refresh token");
        }
    }

    @Override
    public Void forgotPassword(@NonNull AuthForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());

        userRepository.findByEmail(email).ifPresent(user -> {
            try {
                String resetToken = jwtService.createResetToken(user);
                String resetLink = UriComponentsBuilder.fromUriString(clientProperties.url() + "/reset-password")
                        .queryParam("token", resetToken)
                        .build()
                        .toUriString();


                Map<String, Object> props = new HashMap<>();
                props.put("name", user.getName());
                props.put("resetPasswordLink", resetLink);
                props.put("expirationMinutes", Duration.ofMillis(jwtProperties.resetExpiration()).toMinutes());

                MailDto dataMail = mailService.createMailDto(
                    email,
                    MailTemplate.RESET_PASSWORD,
                    props
                );

                mailService.sendMail(dataMail);
            } catch (Exception e) {
                log.error("Failed to send password reset email to {}", email, e);
            }
        });

        return null;
    }

    @Override
    public Void resetPassword(@NonNull AuthResetPasswordRequest request) {
        String email = jwtService.extractUsername(request.resetToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(request.resetToken(), userDetails)) {
            throw new JwtException("Invalid token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, email));

        if (!jwtService.matchesStoredToken(user, request.resetToken(), TokenType.RESET_TOKEN, null)) {
            throw new JwtException("Invalid token");
        }

        user.setResetToken(null);
        user.setPassword(passwordEncoder.encode(request.password()));

        if (user.getStatus() == UserStatus.PENDING) {
            user.getStatus().validateTransitionTo(UserStatus.ACTIVE);
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);

        return null;
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

