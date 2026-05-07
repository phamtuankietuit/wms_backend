package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.server")
public record ServerProperties(
        @NotBlank
        String url
) {
}
