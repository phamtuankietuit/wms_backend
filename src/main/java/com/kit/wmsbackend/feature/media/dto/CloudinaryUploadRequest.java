package com.kit.wmsbackend.feature.media.dto;

import com.kit.wmsbackend.enums.MediaResourceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CloudinaryUploadRequest(
        @NotNull
        MultipartFile file,

        @NotNull
        MediaResourceType resourceType,

        @Size(max = 255)
        String publicId,

        @Size(max = 255)
        String folder,

        Boolean overwrite
) {
    public boolean shouldOverwrite() {
        return Boolean.TRUE.equals(overwrite);
    }
}
