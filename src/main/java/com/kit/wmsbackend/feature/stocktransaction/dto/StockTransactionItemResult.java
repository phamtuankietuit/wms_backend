package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.enums.AdjustmentType;

public record StockTransactionItemResult(
        Variant variant,
        Integer quantity,
        AdjustmentType adjustmentType
) {
}
