package com.kit.wmsbackend.feature.product.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductInfoResponse(
        UUID id,
        String code,
        String name,
        String description,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
