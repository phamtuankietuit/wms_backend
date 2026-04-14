package com.kit.wmsbackend.feature.product.dto;

import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.variant.dto.VariantResponse;

import java.util.List;

public record ProductCreateResponse(
        ProductInfoResponse productInfo,
        List<AttributeResponse> attributes,
        List<VariantResponse> variants
) {
}