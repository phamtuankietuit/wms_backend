package com.kit.wmsbackend.feature.userwarehouse.dto;

import java.time.OffsetDateTime;

public record UserWarehouseResponse(
        WarehouseUWResponse warehouse,
        OffsetDateTime assignedAt
) {
}
