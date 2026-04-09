package com.kit.wmsbackend.feature.product.dto;

import com.kit.wmsbackend.feature.variant.dto.VariantRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductCreateRequest(
        @NotNull @Valid ProductInfoRequest productInfo,
        List<@Valid ProductAttributeRequest> attributes,
        @NotEmpty List<@Valid VariantRequest> variants
) {
}