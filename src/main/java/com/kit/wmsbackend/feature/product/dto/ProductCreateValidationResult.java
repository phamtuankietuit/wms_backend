package com.kit.wmsbackend.feature.product.dto;

import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.feature.variant.dto.VariantRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductCreateValidationResult(
        String normalizedCode,
        ProductInfoRequest productInfo,
        List<ProductCreateAttributeContext> attributeContexts,
        Map<UUID, AttributeValue> selectedValuesById,
        List<List<UUID>> combinations,
        Map<String, VariantRequest> variantRequestMap
) {
}
