package com.kit.wmsbackend.feature.media.service;

import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.feature.media.dto.CloudinaryDeleteResponse;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadRequest;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;

public interface CloudinaryService {
    CloudinaryUploadResponse upload(@Valid @NotNull CloudinaryUploadRequest request);

    CloudinaryDeleteResponse delete(
            @NotBlank String publicId,
            @NotNull MediaResourceType resourceType
    );

    List<CloudinaryDeleteResponse> deleteAll(
            @NotEmpty Collection<@NotBlank String> publicIds,
            @NotNull MediaResourceType resourceType
    );
}
