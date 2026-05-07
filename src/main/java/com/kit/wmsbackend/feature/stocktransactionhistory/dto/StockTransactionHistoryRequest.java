package com.kit.wmsbackend.feature.stocktransactionhistory.dto;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.enums.StockTransactionStatus;
import jakarta.validation.constraints.NotNull;

public record StockTransactionHistoryRequest(
        @NotNull
        StockTransaction stockTransaction,

        @NotNull
        StockTransactionStatus fromStatus,

        @NotNull
        StockTransactionStatus toStatus,

        String note,

        String reason
) {
}
