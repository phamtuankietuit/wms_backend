package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest authLoginRequest);
    AuthRegisterResponse register(AuthRegisterRequest authRegisterRequest);
    AuthRefreshTokenResponse refreshToken(HttpServletRequest request);
}
