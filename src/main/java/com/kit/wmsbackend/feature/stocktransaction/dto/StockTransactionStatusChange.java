package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.enums.StockTransactionStatus;

public record StockTransactionStatusChange(
        StockTransaction stockTransaction,
        StockTransactionStatus nextStatus
) {
}
