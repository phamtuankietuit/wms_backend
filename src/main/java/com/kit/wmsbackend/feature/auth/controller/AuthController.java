package com.kit.wmsbackend.feature.auth.controller;

import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.service.AuthService;
import com.kit.wmsbackend.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(
        @Valid @RequestBody AuthLoginRequest authLoginRequest
    ) {
        AuthLoginResponse loginResponse = authService.login(authLoginRequest);

        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthRefreshTokenResponse>> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        AuthRefreshTokenResponse refreshTokenResponse = authService.refreshToken(bearerToken);
        return ResponseEntity.ok(ApiResponse.success(refreshTokenResponse));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody AuthForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
        @Valid @RequestBody AuthResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthGetMeResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success("User info retrieved successfully", authService.getMe()));
    }
}
