package com.kit.wmsbackend.feature.auth.dto;

import com.kit.wmsbackend.constant.RegexConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthResetPasswordRequest(
    @NotBlank(message = "Reset token is required")
    String resetToken,

    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password must be at most 72 characters")
    @Pattern(
            regexp = RegexConstant.PASSWORD_REGEX,
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long"
    )
    String password
) {
}
