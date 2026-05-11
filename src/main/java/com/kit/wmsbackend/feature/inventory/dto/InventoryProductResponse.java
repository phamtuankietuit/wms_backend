package com.kit.wmsbackend.feature.inventory.dto;

import java.util.UUID;

public record InventoryProductResponse(
        UUID id,
        String code,
        String name
) {
}
