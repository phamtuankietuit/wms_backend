package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.wmsbackend.feature.permission.dto.PermissionResponse;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleUpdateResponse(
        UUID id,
        String name,
        String code,
        Boolean isAdminRole,
        Boolean isSystemRole,
        Set<PermissionResponse> permissions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
