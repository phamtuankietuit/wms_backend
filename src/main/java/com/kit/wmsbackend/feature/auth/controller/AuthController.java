package com.kit.wmsbackend.feature.auth.controller;

import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.service.AuthService;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    CookieUtils cookieUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody AuthLoginRequest authLoginRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        AuthLoginResponse loginResponse = authService.login(authLoginRequest, request);
        cookieUtils.addAccessTokenCookie(response, loginResponse.accessToken());
        cookieUtils.addRefreshTokenCookie(response, loginResponse.refreshToken());

        return ResponseEntity.ok(ApiResponse.success("Login successful", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        AuthRefreshTokenResponse refreshTokenResponse = authService.refreshToken(request);
        cookieUtils.addAccessTokenCookie(response, refreshTokenResponse.accessToken());
        cookieUtils.addRefreshTokenCookie(response, refreshTokenResponse.refreshToken());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody AuthForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.forgotPassword(request)));
    }

    @PostMapping("reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
        @Valid @RequestBody AuthResetPasswordRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.resetPassword(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthGetMeResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success("User info retrieved successfully", authService.getMe()));
    }
}
