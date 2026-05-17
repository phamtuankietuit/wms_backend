package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleResponse (
        UUID id,
        String name,
        String code,
        @JsonProperty("isAdminRole") boolean adminRole,
        @JsonProperty("isSystemRole") boolean systemRole,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
