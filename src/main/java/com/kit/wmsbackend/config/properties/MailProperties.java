package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        @NotBlank
        String name,

        @NotBlank
        String username
) {
}
