package com.kit.wmsbackend.feature.variant.dto;

import java.util.UUID;

public record VariantResponse(
        UUID productId,
        String sku,
        Boolean isActive
) {
}
