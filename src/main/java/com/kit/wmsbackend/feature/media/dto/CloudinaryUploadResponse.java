package com.kit.wmsbackend.feature.media.dto;

import com.kit.wmsbackend.enums.MediaResourceType;

public record CloudinaryUploadResponse(
        String publicId,
        String secureUrl,
        MediaResourceType resourceType,
        String format,
        Long bytes,
        Integer width,
        Integer height,
        String originalFilename
) {
}
