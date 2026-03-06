package com.kit.wmsbackend.feature.userwarehouse.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserWarehouseAssignRequest(
        @NotNull(message = "User id is required")
        UUID userId,
        @NotNull(message = "Warehouse id is required")
        UUID warehouseId
) {
}
