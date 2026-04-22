package com.kit.wmsbackend.feature.stocktransaction.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StockTransactionItemResponse(
        UUID id,
        Integer quantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
