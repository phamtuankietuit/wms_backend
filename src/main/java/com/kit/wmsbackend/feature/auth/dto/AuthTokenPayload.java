package com.kit.wmsbackend.feature.auth.dto;

public record AuthTokenPayload(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}

