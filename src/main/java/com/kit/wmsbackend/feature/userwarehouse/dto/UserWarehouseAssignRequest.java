package com.kit.wmsbackend.feature.userwarehouse.dto;

import jakarta.validation.constraints.NotBlank;

public record UserWarehouseAssignRequest(
        @NotBlank(message = "User id is required")
        String userId,
        @NotBlank(message = "Warehouse id is required")
        String warehouseId
) {
}
