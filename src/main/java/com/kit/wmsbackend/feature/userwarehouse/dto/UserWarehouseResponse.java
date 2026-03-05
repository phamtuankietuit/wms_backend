package com.kit.wmsbackend.feature.userwarehouse.dto;

import java.time.Instant;
import java.util.UUID;

public record UserWarehouseResponse(
        UUID userId,
        UUID warehouseId,
        Instant assignedAt
) {
}
