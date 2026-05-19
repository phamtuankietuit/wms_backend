package com.kit.wmsbackend.feature.stocktransaction.dto;

import java.util.UUID;

public record InventoryKey(
        UUID variantId,
        UUID warehouseId
) {
}
