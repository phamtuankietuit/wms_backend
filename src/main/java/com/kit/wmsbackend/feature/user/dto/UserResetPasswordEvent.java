package com.kit.wmsbackend.feature.user.dto;

public record UserResetPasswordEvent(
        String email,
        String name,
        String resetToken
) {
}
