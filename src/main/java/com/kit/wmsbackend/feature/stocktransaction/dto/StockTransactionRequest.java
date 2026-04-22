package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.enums.StockTransactionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record StockTransactionRequest(
        @NotNull
        UUID warehouseId,

        @NotNull
        StockTransactionType type,

        @Size(max = 1000, message = "Note must be at most 1000 characters")
        String note,

        @NotEmpty @Valid
        List<StockTransactionItemRequest> items
) {
}
