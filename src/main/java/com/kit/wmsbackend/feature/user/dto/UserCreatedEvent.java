package com.kit.wmsbackend.feature.user.dto;

import java.util.UUID;

public record UserCreatedEvent(
        UUID userId,
        String email,
        String name
) {
}
