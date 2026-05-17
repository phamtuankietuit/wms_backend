package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kit.wmsbackend.dto.Auditor;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RoleListResponse(
        UUID id,
        String name,
        String code,
        @JsonProperty("isAdminRole") boolean adminRole,
        @JsonProperty("isSystemRole") boolean systemRole,
        Auditor creator,
        Auditor updater,
        Auditor deleter,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime deletedAt
) {
}
