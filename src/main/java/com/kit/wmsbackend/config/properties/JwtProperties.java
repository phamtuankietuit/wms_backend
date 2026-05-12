package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        @NotBlank
        String secret,

        @Min(1)
        Long expiration,

        @Min(1)
        Long refreshExpiration,

        @Min(1)
        Long resetExpiration,

        @Min(1)
        Long onboardingResetExpiration
) {
}
