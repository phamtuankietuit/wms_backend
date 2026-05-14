package com.kit.wmsbackend.feature.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserInfoUpdateRequest(
        @NotBlank(message = "Name is required")
        String name,

        LocalDate dateOfBirth,

        String avatar
) {
}

