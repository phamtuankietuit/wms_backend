package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.wmsbackend.dto.Auditor;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleListResponse(
        UUID id,
        String name,
        String code,
        Boolean isAdminRole,
        Boolean isSystemRole,
        Auditor creator,
        Auditor updater,
        Auditor deleter,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime deletedAt
) {
}
