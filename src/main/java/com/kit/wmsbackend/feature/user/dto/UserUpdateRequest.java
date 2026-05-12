package com.kit.wmsbackend.feature.user.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record UserUpdateRequest (
        String email,
        String password,
        String name,
        LocalDate dateOfBirth,
        String avatar,
        Set<UUID> roleIds
) {
}

