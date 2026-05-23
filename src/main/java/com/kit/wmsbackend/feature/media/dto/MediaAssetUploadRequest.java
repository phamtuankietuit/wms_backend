package com.kit.wmsbackend.feature.media.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record MediaAssetUploadRequest(
        @NotNull
        MultipartFile file,

        String publicId,

        Boolean isPrimary,

        @NotNull
        Boolean overwrite,

        @NotNull
        Boolean invalidate
) {
    public boolean primaryRequested() {
        return Boolean.TRUE.equals(isPrimary);
    }
}
