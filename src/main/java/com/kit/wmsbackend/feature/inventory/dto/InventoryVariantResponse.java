package com.kit.wmsbackend.feature.inventory.dto;

import java.util.UUID;

public record InventoryVariantResponse(
        UUID id,
        String sku,
        InventoryProductResponse product
) {
}
