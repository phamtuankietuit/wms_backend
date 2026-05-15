package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleResponse (
        UUID id,
        String name,
        String code,
        Boolean isAdminRole,
        Boolean isSystemRole,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
