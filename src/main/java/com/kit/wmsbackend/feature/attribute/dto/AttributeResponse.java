package com.kit.wmsbackend.feature.attribute.dto;

import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttributeResponse(
        UUID id,

        String code,

        String name,

        Boolean isActive,

        OffsetDateTime createdAt,

        OffsetDateTime updatedAt,

        List<AttributeValueResponse> attributeValues
) {
}
