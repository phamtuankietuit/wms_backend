package com.kit.wmsbackend.feature.inventorymovement.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        StockTransactionIMResponse stockTransaction,
        Long quantityChange,
        Long beforeQuantity,
        Long afterQuantity,
        OffsetDateTime createdAt
) {
}
