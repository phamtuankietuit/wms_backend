package com.kit.wmsbackend.feature.auth.dto;

import java.util.UUID;

public record AuthRegisterResponse(
        UUID userId,
        String email,
        String name,
        AuthTokenPayload token
) {
}

