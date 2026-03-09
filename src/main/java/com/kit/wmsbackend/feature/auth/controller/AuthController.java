package com.kit.wmsbackend.feature.auth.controller;

import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterResponse;
import com.kit.wmsbackend.feature.auth.service.AuthService;
import com.kit.wmsbackend.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest authLoginRequest) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        authService.login(authLoginRequest)
                ));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthRegisterResponse>> register(@Valid @RequestBody AuthRegisterRequest authRegisterRequest) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Register successful",
                        authService.register(authRegisterRequest)
                ));
    }
}
