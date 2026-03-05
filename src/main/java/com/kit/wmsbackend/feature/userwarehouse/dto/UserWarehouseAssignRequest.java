package com.kit.wmsbackend.feature.userwarehouse.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserWarehouseAssignRequest(
        @NotBlank(message = "User id is required")
        UUID userId,
        @NotBlank(message = "Warehouse id is required")
        UUID warehouseId
) {
}
