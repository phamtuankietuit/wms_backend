package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.entity.RefreshToken;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.MailTemplate;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.model.UserPrincipal;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.refreshtoken.repository.RefreshTokenRepository;
import com.kit.wmsbackend.feature.refreshtoken.service.RefreshTokenService;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import com.kit.wmsbackend.utils.CookieUtils;
import com.kit.wmsbackend.utils.SecurityUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final MailService mailService;
    private final CookieUtils cookieUtils;

    @Value("${app.client.url}")
    private String clientUrl;

    @Value("${app.security.jwt.reset-expiration}")
    private long resetExpiration;

    @Override
    @Transactional
    public Void login(
        @NonNull AuthLoginRequest authLoginRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String normalizedEmail = normalizeEmail(authLoginRequest.email());

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                authLoginRequest.password()
        );

        authenticationManager.authenticate(authenticationToken);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

        String jti = UUID.randomUUID().toString();

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user, jti, request);

        cookieUtils.addAccessTokenCookie(response, accessToken);
        cookieUtils.addRefreshTokenCookie(response, refreshToken);

        return null;
    }

    @Override
    @Transactional
    public Void refreshToken(@NonNull HttpServletRequest request, HttpServletResponse response) {
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

            RefreshToken refreshToken = refreshTokenService.findActiveByJti(jti);
            if (refreshToken != null) {
                refreshToken.setLastUsedAt(LocalDateTime.now());
                refreshTokenRepository.save(refreshToken);
            }

            String accessToken = jwtService.createAccessToken(user);

            cookieUtils.addAccessTokenCookie(response, accessToken);
        } else {
            throw new JwtException("Invalid refresh token");
        }

        return null;
    }

    @Override
    public Void forgotPassword(@NonNull AuthForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());

        userRepository.findByEmail(email).ifPresent(user -> {
            try {
                String resetToken = jwtService.createResetToken(user);
                String resetLink = UriComponentsBuilder.fromUriString(clientUrl + "/reset-password")
                        .queryParam("token", resetToken)
                        .build()
                        .toUriString();


                Map<String, Object> props = new HashMap<>();
                props.put("name", user.getName());
                props.put("resetPasswordLink", resetLink);
                props.put("expirationMinutes", Duration.ofMillis(resetExpiration).toMinutes());

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
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!jwtService.matchesStoredToken(user, request.resetToken(), TokenType.RESET_TOKEN, null)) {
            throw new JwtException("Invalid token");
        }

        user.setResetToken(null);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return null;
    }

    @Override
    public AuthGetMeResponse getMe() {
        UserPrincipal userPrincipal = SecurityUtils.getCurrentUser();

        User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userPrincipal.getUsername()));

        return authMapper.toAuthGetMeResponse(user);
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

