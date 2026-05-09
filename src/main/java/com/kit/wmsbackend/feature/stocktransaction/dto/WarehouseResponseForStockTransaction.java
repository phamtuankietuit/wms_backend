package com.kit.wmsbackend.feature.stocktransaction.dto;

import java.util.UUID;

public record WarehouseResponseForStockTransaction(
        UUID id,
        String code,
        String name
) {
}
