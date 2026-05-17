package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kit.wmsbackend.feature.permission.dto.PermissionResponse;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record RoleDetailResponse(
        UUID id,
        String name,
        String code,
        @JsonProperty("isAdminRole") boolean adminRole,
        @JsonProperty("isSystemRole") boolean systemRole,
        Set<PermissionResponse> permissions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
