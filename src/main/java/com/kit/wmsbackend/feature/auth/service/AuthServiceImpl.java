package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager;
    JwtService jwtService;
    UserRepository userRepository;
    UserDetailsService userDetailsService;
    PasswordEncoder passwordEncoder;
    AuthMapper authMapper;

    @Override
    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        var authenticationToken = new UsernamePasswordAuthenticationToken(
            normalizedEmail,
            request.password()
        );

        var authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new JwtException("User not found"));

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthLoginResponse(buildTokenPayload(accessToken, refreshToken));
    }

    @Override
    @Transactional
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = authMapper.toUser(request);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return authMapper.toAuthRegisterResponse(savedUser, buildTokenPayload(accessToken, refreshToken));
    }

    @Override
    public AuthRefreshTokenResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Invalid refresh token");
        }

        final String jwt = authHeader.substring(7);

        User user = userRepository
                .findByRefreshToken(jwt)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                String refreshToken = user.getRefreshToken();

                return new AuthRefreshTokenResponse(buildTokenPayload(accessToken, refreshToken));
            }
        }

        throw new JwtException("Invalid refresh token");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthTokenPayload buildTokenPayload(String accessToken, String refreshToken) {
        return new AuthTokenPayload(accessToken, "Bearer", refreshToken);
    }
}

