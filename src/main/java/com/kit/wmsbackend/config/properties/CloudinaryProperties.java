package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.cloudinary")
public record CloudinaryProperties(
        @NotBlank(message = "Cloudinary cloud name must not be blank")
        @Pattern(regexp = "^(?!\\$\\{).+$", message = "Cloudinary cloud name must be configured")
        String cloudName,

        @NotBlank(message = "Cloudinary API key must not be blank")
        @Pattern(regexp = "^(?!\\$\\{).+$", message = "Cloudinary API key must be configured")
        String apiKey,

        @NotBlank(message = "Cloudinary API secret must not be blank")
        @Pattern(regexp = "^(?!\\$\\{).+$", message = "Cloudinary API secret must be configured")
        String apiSecret,

        Boolean secure,

        @NotBlank(message = "Cloudinary folder must not be blank")
        @Size(max = 255, message = "Cloudinary folder must be at most 255 characters")
        @Pattern(
                regexp = "^(?!/)(?!.*\\.\\.)(?!.*\\\\)[A-Za-z0-9_./-]+(?<!/)$",
                message = "Cloudinary folder contains unsafe characters"
        )
        String folder,

        @NotNull(message = "Cloudinary max file size must be configured")
        @Min(value = 1, message = "Cloudinary max file size must be positive")
        Long maxFileSizeBytes,

        @NotEmpty(message = "Allowed image content types must not be empty")
        List<@NotBlank String> allowedImageContentTypes,

        @NotEmpty(message = "Allowed video content types must not be empty")
        List<@NotBlank String> allowedVideoContentTypes,

        @NotEmpty(message = "Allowed raw content types must not be empty")
        List<@NotBlank String> allowedRawContentTypes
) {
    public boolean secureEnabled() {
        return secure == null || secure;
    }
}
