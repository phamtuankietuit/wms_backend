package com.kit.wmsbackend.feature.auth.dto;

public record AuthRefreshTokenResponse(
        AuthTokenPayload token
) {
}
