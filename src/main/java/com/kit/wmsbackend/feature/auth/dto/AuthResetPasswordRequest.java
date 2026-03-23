package com.kit.wmsbackend.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthResetPasswordRequest(
    @NotBlank(message = "Reset token is required")
    String resetToken,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    String password
) {
}
