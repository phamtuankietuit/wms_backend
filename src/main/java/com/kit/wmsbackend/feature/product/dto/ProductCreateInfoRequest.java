package com.kit.wmsbackend.feature.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCreateInfoRequest(
        @NotBlank(message = "Product code must not be blank")
        @Size(max = 100, message = "Product code must not exceed 100 characters")
        String code,

        @NotBlank(message = "Product name must not be blank")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        String description,

        Boolean isActive
) {
}