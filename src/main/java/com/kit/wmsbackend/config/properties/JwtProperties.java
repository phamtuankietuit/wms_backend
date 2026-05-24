package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        @NotBlank
        @Size(min = 32, message = "JWT secret must be at least 32 characters")
        String secret,

        @NotNull
        @Min(1)
        Long expiration,

        @NotNull
        @Min(1)
        Long refreshExpiration,

        @NotNull
        @Min(1)
        Long resetExpiration,

        @NotNull
        @Min(1)
        Long onboardingResetExpiration
) {
}
