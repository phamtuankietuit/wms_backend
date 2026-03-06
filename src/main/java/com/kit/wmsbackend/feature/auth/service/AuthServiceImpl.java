package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthTokenPayload;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.mapper.AuthMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    public AuthLoginResponse login(AuthLoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        var authenticationToken = new UsernamePasswordAuthenticationToken(
            normalizedEmail,
            request.password()
        );

        var authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new BadCredentialsException(null));

        AuthTokenPayload tokenPayload = resolveTokenPayload(user, userDetails);

        return new AuthLoginResponse(user.getId(), user.getEmail(), user.getName(), tokenPayload);
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
        AuthTokenPayload tokenPayload = resolveTokenPayload(savedUser, userDetails);

        return authMapper.toAuthRegisterResponse(savedUser, tokenPayload);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthTokenPayload buildTokenPayload(UserDetails userDetails) {
        String accessToken = jwtService.generateToken(userDetails);
        return new AuthTokenPayload(accessToken, "Bearer", jwtService.getExpirationMs() / 1000);
    }

    private AuthTokenPayload resolveTokenPayload(User user, UserDetails userDetails) {
        String existingToken = user.getAccessToken();
        if (existingToken != null && !existingToken.isBlank()) {
            return new AuthTokenPayload(existingToken, "Bearer", jwtService.getExpirationMs() / 1000);
        }

        AuthTokenPayload tokenPayload = buildTokenPayload(userDetails);
        user.setAccessToken(tokenPayload.accessToken());
        userRepository.save(user);
        return tokenPayload;
    }
}

