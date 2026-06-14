package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthService {
    AuthLoginResponse login(@Valid AuthLoginRequest authLoginRequest, HttpServletRequest request);
    AuthRefreshTokenResponse refreshToken(String bearerToken);
    void forgotPassword(@Valid AuthForgotPasswordRequest request);
    void resetPassword(@Valid AuthResetPasswordRequest request);
    AuthGetMeResponse getMe();
}
