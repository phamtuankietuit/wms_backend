package com.kit.wmsbackend.feature.auth.dto;

public record AuthLoginResponse(
        String accessToken,
        String refreshToken
) {
}

