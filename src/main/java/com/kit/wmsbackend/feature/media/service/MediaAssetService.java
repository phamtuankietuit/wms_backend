package com.kit.wmsbackend.feature.media.service;

import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import com.kit.wmsbackend.feature.media.dto.MediaAssetUploadRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface MediaAssetService {
    List<MediaAssetResponse> listImages(
            @NotNull MediaOwnerType ownerType,
            @NotNull UUID ownerId
    );

    MediaAssetResponse uploadImage(
            @NotNull MediaOwnerType ownerType,
            @NotNull UUID ownerId,
            @Valid @NotNull MediaAssetUploadRequest request
    );

    MediaAssetResponse setPrimaryImage(
            @NotNull MediaOwnerType ownerType,
            @NotNull UUID ownerId,
            @NotNull UUID imageId
    );

    void deleteImage(
            @NotNull MediaOwnerType ownerType,
            @NotNull UUID ownerId,
            @NotNull UUID imageId
    );

    void bulkDeleteImages(
            @NotNull MediaOwnerType ownerType,
            @NotNull UUID ownerId,
            @NotEmpty Set<@NotNull UUID> imageIds
    );

    Map<UUID, String> findPrimaryImageUrls(
            @NotNull MediaOwnerType ownerType,
            @NotNull Collection<UUID> ownerIds
    );
}
