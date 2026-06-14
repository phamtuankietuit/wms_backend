package com.kit.wmsbackend.feature.refreshtoken.dto;

import com.kit.wmsbackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record RefreshTokenRequest(
        @NonNull
        User user,

        @NotBlank
        String jti,

        @NotBlank
        String sessionId,

        @NotBlank
        String rawToken
) {
}
