package com.kit.wmsbackend.feature.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProductAttributeRequest(
        @NotNull UUID attributeId,
        @NotEmpty List<UUID> attributeValueIds
) {
}