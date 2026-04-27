package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.enums.StockTransactionType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StockTransactionResponse(
        UUID id,
        UUID assignedTo,
        UUID warehouseId,
        StockTransactionType type,
        StockTransactionStatus status,
        String note,
        List<StockTransactionItemResponse> stockTransactionItems,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
