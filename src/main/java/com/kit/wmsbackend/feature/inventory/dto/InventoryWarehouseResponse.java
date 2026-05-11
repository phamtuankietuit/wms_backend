package com.kit.wmsbackend.feature.inventory.dto;

import java.util.UUID;

public record InventoryWarehouseResponse(
        UUID id,
        String code,
        String name
) {
}
