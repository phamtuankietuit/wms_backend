package com.kit.wmsbackend.feature.product.dto;

import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;

public record ProductAttributeResponse(
        AttributeResponse attribute,
        Boolean isActive
) {
}
