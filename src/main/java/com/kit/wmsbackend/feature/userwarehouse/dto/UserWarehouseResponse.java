package com.kit.wmsbackend.feature.userwarehouse.dto;

import java.time.Instant;

public record UserWarehouseResponse(
        String userId,
        String warehouseId,
        Instant assignedAt
) {
}
