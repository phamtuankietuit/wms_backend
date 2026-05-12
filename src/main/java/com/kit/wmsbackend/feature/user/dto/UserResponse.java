package com.kit.wmsbackend.feature.user.dto;

import com.kit.wmsbackend.enums.UserStatus;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse (
        UUID id,
        String code,
        String email,
        String name,
        LocalDate dateOfBirth,
        String avatar,
        UserStatus status,
        Set<RoleResponse> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

