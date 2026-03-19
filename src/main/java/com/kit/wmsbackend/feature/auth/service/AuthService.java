package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    Void login(@Valid AuthLoginRequest authLoginRequest, HttpServletResponse response);
    AuthRegisterResponse register(@Valid AuthRegisterRequest authRegisterRequest);
    Void refreshToken(HttpServletRequest request, HttpServletResponse response);
    Void forgotPassword(@Valid AuthForgotPasswordRequest request);
    Void resetPassword(String resetToken, @Valid AuthResetPasswordRequest request);
}
