package com.kit.wmsbackend.feature.role.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest (
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Code is required")
        String code,

        Boolean isAdminRole,

        Boolean isSystemRole
) {
}
