package com.kit.wmsbackend.feature.inventory.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        InventoryVariantResponse variant,
        InventoryWarehouseResponse warehouse,
        Long quantity,
        Long reservedQuantity,
        Long availableQuantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
