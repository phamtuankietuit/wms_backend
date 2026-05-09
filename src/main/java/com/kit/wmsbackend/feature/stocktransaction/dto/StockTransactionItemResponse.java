package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.enums.AdjustmentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record StockTransactionItemResponse(
        UUID id,
        String variantSku,
        String productName,
        String productCode,
        Integer quantity,
        AdjustmentType adjustmentType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
