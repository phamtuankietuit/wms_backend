package com.kit.wmsbackend.feature.auth.dto;

public record AuthLoginResponse(
        String userId,
        String email,
        String name,
        AuthTokenPayload token
) {
}

