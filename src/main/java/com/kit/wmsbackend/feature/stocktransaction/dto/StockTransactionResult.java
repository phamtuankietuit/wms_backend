package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.enums.StockTransactionType;

import java.util.List;

public record StockTransactionResult(
        Warehouse warehouse,
        User assignedTo,
        StockTransactionType type,
        String note,
        List<StockTransactionItemResult> items
) {
}
