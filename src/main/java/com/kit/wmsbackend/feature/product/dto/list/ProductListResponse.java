package com.kit.wmsbackend.feature.product.dto.list;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductListResponse(
        UUID id,
        String code,
        String name,
        String imageUrl,
        String description,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
