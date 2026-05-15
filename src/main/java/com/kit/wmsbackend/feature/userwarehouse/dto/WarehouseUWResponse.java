package com.kit.wmsbackend.feature.userwarehouse.dto;

import java.util.UUID;

public record WarehouseUWResponse(
        UUID id,
        String name,
        String code
) {
}
