package com.kit.wmsbackend.feature.auth.dto;

public record AuthRegisterResponse(
        String userId,
        String email,
        String name,
        AuthTokenPayload token
) {
}

