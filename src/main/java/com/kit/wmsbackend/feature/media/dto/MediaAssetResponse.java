package com.kit.wmsbackend.feature.media.dto;

import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MediaAssetResponse(
        UUID id,
        MediaOwnerType ownerType,
        UUID ownerId,
        String publicId,
        String secureUrl,
        MediaResourceType resourceType,
        String format,
        Long bytes,
        Integer width,
        Integer height,
        String originalFilename,
        Integer sortOrder,
        Boolean isPrimary,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
