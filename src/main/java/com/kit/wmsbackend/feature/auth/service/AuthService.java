package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AuthService {
    Void login(@Valid AuthLoginRequest authLoginRequest, HttpServletRequest request, HttpServletResponse response);
    Void refreshToken(HttpServletRequest request, HttpServletResponse response);
    Void forgotPassword(@Valid AuthForgotPasswordRequest request);
    Void resetPassword(@Valid AuthResetPasswordRequest request);
    AuthGetMeResponse getMe();
}
