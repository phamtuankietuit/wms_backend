package com.kit.wmsbackend.feature.variant.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record VariantResponse(
        UUID id,
        String sku,
        Boolean isActive,
        Boolean isDefault,
        List<UUID> attributeValueIds,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
