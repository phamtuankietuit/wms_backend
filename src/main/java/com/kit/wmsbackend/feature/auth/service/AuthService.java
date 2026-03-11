package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthService {
    AuthLoginResponse login(@Valid AuthLoginRequest authLoginRequest);
    AuthRegisterResponse register(@Valid AuthRegisterRequest authRegisterRequest);
    AuthRefreshTokenResponse refreshToken(HttpServletRequest request);
    Void forgotPassword(@Valid AuthForgotPasswordRequest request);
    Void resetPassword(String resetToken, @Valid AuthResetPasswordRequest request);
}
