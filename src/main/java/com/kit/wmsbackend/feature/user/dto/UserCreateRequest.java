package com.kit.wmsbackend.feature.user.dto;

import com.kit.wmsbackend.constant.RegexConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record UserCreateRequest (
        @NotBlank(message = "Email is required")
        @Email(regexp = RegexConstant.EMAIL_REGEX, message = "Email must be valid")
        String email,

        @NotBlank(message = "Name is required")
        String name,

        LocalDate dateOfBirth,

        String avatar,

        @NotEmpty(message = "At least one role must be assigned")
        Set<@NotNull(message = "Role ids must be not null") UUID> roleIds,

        @NotEmpty(message = "At least one warehouse must be assigned")
        Set<@NotNull(message = "Warehouse ids must be not null") UUID> warehouseIds
) {
}

