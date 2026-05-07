package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.wmsbackend.dto.Auditor;
import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.enums.StockTransactionType;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StockTransactionResponse(
        UUID id,
        AssignedToResponse assignedTo,
        WarehouseResponseForStockTransaction warehouse,
        String code,
        StockTransactionType type,
        StockTransactionStatus status,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Auditor creator,
        Auditor updater,
        Auditor deleter
) {
}
