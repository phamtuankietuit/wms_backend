package com.kit.wmsbackend.feature.stocktransactionhistory.dto;

import com.kit.wmsbackend.dto.Auditor;
import com.kit.wmsbackend.enums.StockTransactionStatus;

import java.time.OffsetDateTime;

public record StockTransactionHistoryResponse(
        StockTransactionStatus fromStatus,
        StockTransactionStatus toStatus,
        Auditor assignedTo,
        String note,
        String reason,
        Auditor creator,
        OffsetDateTime createdAt
) {
}
