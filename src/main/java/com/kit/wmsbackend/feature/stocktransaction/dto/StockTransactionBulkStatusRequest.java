package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.enums.StockTransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record StockTransactionBulkStatusRequest(
        @NotEmpty(message = "Stock transaction ids are required")
        Set<@NotNull(message = "Stock transaction id is required") UUID> ids,

        @NotNull(message = "Status is required")
        StockTransactionStatus nextStatus,

        @NotBlank(message = "Reason is required")
        @Size(max = 1000, message = "Reason must be at most 1000 characters")
        String reason,

        @Size(max = 500, message = "Note must be at most 500 characters")
        String note
) {
}
