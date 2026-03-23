package com.kit.wmsbackend.feature.auth.controller;

import com.kit.wmsbackend.feature.auth.dto.*;
import com.kit.wmsbackend.feature.auth.service.AuthService;
import com.kit.wmsbackend.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody AuthLoginRequest authLoginRequest,
        HttpServletResponse response
    ) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(authLoginRequest, response)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthRegisterResponse>> register(@Valid @RequestBody AuthRegisterRequest authRegisterRequest) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Register successful",
                        authService.register(authRegisterRequest)
                ));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request, response)));
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
}
