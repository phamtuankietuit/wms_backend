package com.kit.wmsbackend.feature.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest (
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Code is required")
        @Size(max = 200, message = "Code must be at most 200 characters")
        String code
) {
}
