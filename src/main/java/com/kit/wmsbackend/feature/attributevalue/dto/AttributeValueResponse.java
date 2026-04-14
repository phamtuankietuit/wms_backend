package com.kit.wmsbackend.feature.attributevalue.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AttributeValueResponse(
        UUID id,
        String code,
        String value,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
