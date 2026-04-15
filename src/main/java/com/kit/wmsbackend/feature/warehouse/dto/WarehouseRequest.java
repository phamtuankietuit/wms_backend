package com.kit.wmsbackend.feature.warehouse.dto;

import jakarta.validation.constraints.NotBlank;

public record WarehouseRequest(
        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Name is required")
        String name,

        Boolean isActive
) {
}
