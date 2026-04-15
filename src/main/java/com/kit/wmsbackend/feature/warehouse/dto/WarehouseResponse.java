package com.kit.wmsbackend.feature.warehouse.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String code,
        String name,
        Boolean isActive,
        String address,
        String phone,
        String email,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
