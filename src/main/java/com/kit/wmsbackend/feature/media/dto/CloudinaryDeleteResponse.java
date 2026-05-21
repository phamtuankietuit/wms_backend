package com.kit.wmsbackend.feature.media.dto;

import com.kit.wmsbackend.enums.MediaResourceType;

public record CloudinaryDeleteResponse(
        String publicId,
        MediaResourceType resourceType,
        boolean deleted,
        String result
) {
}
