package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        @NotBlank
        String name,

        @NotBlank
        String username
) {
}
