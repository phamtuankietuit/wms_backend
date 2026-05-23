package com.kit.wmsbackend.feature.media.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record MediaAssetBulkDeleteRequest(
        @NotEmpty(message = "Image ids must not be empty")
        Set<@NotNull(message = "Image id must not be null") UUID> imageIds
) {
}
