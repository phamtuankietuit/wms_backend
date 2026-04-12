package com.kit.wmsbackend.feature.variant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record VariantRequest(
        @NotBlank(message = "SKU must not be blank")
        @Size(max = 255, message = "SKU must not exceed 255 characters")
        String sku,

        Boolean isActive,

        List<UUID> attributeValueIds
) {
}