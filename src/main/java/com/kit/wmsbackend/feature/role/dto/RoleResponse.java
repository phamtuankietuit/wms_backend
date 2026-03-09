package com.kit.wmsbackend.feature.role.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleResponse (
        String name,
        String code,
        Boolean isAdminRole,
        Boolean isSystemRole,
        String createdAt,
        String updatedAt
) {
}
