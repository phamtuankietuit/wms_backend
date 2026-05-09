package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.enums.AdjustmentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StockTransactionItemRequest(
        @NotNull
        UUID variantId,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        AdjustmentType adjustmentType
) {
}
