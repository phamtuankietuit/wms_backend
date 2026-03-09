package com.kit.wmsbackend.feature.auth.service;

import com.kit.wmsbackend.feature.auth.dto.AuthLoginRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthLoginResponse;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterRequest;
import com.kit.wmsbackend.feature.auth.dto.AuthRegisterResponse;

public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest authLoginRequest);

    AuthRegisterResponse register(AuthRegisterRequest authRegisterRequest);
}
