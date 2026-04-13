package com.kit.wmsbackend.feature.product.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductUpdateInfoRequest(
        @NotBlank(message = "Product name must not be blank")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        String description,

        @NotNull(message = "isActive must not be null")
        Boolean isActive
) {
}
