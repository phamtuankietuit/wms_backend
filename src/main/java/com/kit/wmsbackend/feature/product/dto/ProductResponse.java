package com.kit.wmsbackend.feature.product.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String code,
        String name,
        String description,
        Boolean isActive,
        List<ProductAttributeResponse> productAttributes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
