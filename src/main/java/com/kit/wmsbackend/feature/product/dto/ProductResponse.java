package com.kit.wmsbackend.feature.product.dto;

public record ProductResponse(
        String name,
        String description,
        Boolean isActive,
        String createdAt,
        String updatedAt
) {
}
