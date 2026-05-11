package com.kit.wmsbackend.feature.inventorymovement.dto;

import java.util.UUID;

public record StockTransactionIMResponse(
        UUID id,
        String code
) {
}
