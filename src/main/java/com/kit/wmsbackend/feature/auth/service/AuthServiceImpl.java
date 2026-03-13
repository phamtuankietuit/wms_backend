package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.constant.TemplateMailConstant;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.enums.TokenType;
import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.exception.UserStateInconsistencyException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.mail.dto.MailDto;
import com.kit.wmsbackend.feature.mail.service.MailService;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final MailService mailService;

    @Value("${app.client.url}")
    private String clientUrl;

    @Value("${app.security.jwt.reset-expiration}")
    private long resetExpiration;

    @Override
    @Transactional
    public AuthLoginResponse login(@NonNull AuthLoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                request.password()
        );

        authenticationManager.authenticate(authenticationToken);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UserStateInconsistencyException(
                        "Authenticated user not found in repository"
                ));

        String accessToken = jwtService.createToken(user, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.createToken(user, TokenType.REFRESH_TOKEN);

        return new AuthLoginResponse(buildTokenPayload(accessToken, refreshToken));
    }

    @Override
    @Transactional
    public AuthRegisterResponse register(@NonNull AuthRegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = authMapper.toUser(request);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.createToken(savedUser, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.createToken(savedUser, TokenType.REFRESH_TOKEN);

        return authMapper.toAuthRegisterResponse(savedUser, buildTokenPayload(accessToken, refreshToken));
    }

    @Override
    public AuthRefreshTokenResponse refreshToken(@NonNull HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Invalid refresh token");
        }

        final String jwt = authHeader.substring(7);

        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail == null) {
            throw new JwtException("Invalid refresh token");
        }

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        if (jwtService.isTokenValid(jwt, userDetails) &&
                jwtService.matchesStoredToken(user, jwt, TokenType.REFRESH_TOKEN)) {

            String accessToken = jwtService.createToken(user, TokenType.ACCESS_TOKEN);

            return new AuthRefreshTokenResponse(buildTokenPayload(accessToken));
        }

        throw new JwtException("Invalid refresh token");
    }

    @Override
    public Void forgotPassword(@NonNull AuthForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());

        userRepository.findByEmail(email).ifPresent(user -> {
            try {
                String resetToken = jwtService.createToken(user, TokenType.RESET_TOKEN);
                String resetLink = UriComponentsBuilder.fromUriString(clientUrl + "/reset-password")
                        .queryParam("token", resetToken)
                        .build()
                        .toUriString();

                MailDto dataMail = new MailDto();
                dataMail.setTo(email);
                dataMail.setSubject(TemplateMailConstant.ResetPasswordTemplate.SUBJECT);
                dataMail.setTemplateName(TemplateMailConstant.ResetPasswordTemplate.TEMPLATE_NAME);

                Map<String, Object> props = new HashMap<>();
                props.put("name", user.getName());
                props.put("resetPasswordLink", resetLink);
                props.put("expirationMinutes", Duration.ofMillis(resetExpiration).toMinutes());
                dataMail.setProperties(props);

                mailService.sendMail(dataMail);
            } catch (Exception e) {
                log.error("Failed to send password reset email to {}", email, e);
            }
        });

        return null;
    }

    @Override
    public Void resetPassword(String resetToken, AuthResetPasswordRequest request) {
        String email = jwtService.extractUsername(resetToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(resetToken, userDetails)) {
            throw new JwtException("Invalid token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserStateInconsistencyException("User not found"));

        if (!jwtService.matchesStoredToken(user, resetToken, TokenType.RESET_TOKEN)) {
            throw new JwtException("Invalid token");
        }

        user.setResetToken(null);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return null;
    }

    private @NonNull String normalizeEmail(@NonNull String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthTokenPayload buildTokenPayload(String accessToken) {
        return new AuthTokenPayload(accessToken, "Bearer", null);
    }

    private AuthTokenPayload buildTokenPayload(String accessToken, String refreshToken) {
        return new AuthTokenPayload(accessToken, "Bearer", refreshToken);
    }
}

