package com.kit.wmsbackend.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ImageOptimization(
        @NotNull(message = "Cloudinary image max width must be configured")
        @Min(value = 1, message = "Cloudinary image max width must be positive")
        Integer maxWidth,

        @NotNull(message = "Cloudinary image max height must be configured")
        @Min(value = 1, message = "Cloudinary image max height must be positive")
        Integer maxHeight,

        @NotBlank(message = "Cloudinary image quality must not be blank")
        @Pattern(
                regexp = "^(auto(:best|:good|:eco|:low)?|[1-9][0-9]?|100)$",
                message = "Cloudinary image quality must be auto, auto:best, auto:good, auto:eco, auto:low, or 1-100"
        )
        String quality
) {
}
