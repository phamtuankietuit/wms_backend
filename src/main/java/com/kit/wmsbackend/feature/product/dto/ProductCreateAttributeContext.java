package com.kit.wmsbackend.feature.product.dto;

import com.kit.wmsbackend.entity.Attribute;

import java.util.List;
import java.util.UUID;

public record ProductCreateAttributeContext(
        Attribute attribute,
        List<UUID> attributeValueIds
) {
}
