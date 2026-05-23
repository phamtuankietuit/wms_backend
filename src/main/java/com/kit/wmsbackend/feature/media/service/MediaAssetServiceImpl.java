package com.kit.wmsbackend.feature.media.service;

import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.config.properties.ImageOptimization;
import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.media.dto.CloudinaryDeleteResponse;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadRequest;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadResponse;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import com.kit.wmsbackend.feature.media.dto.MediaAssetUploadRequest;
import com.kit.wmsbackend.feature.media.repository.MediaAssetRepository;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.feature.variant.repository.VariantRepository;
import com.kit.wmsbackend.mapper.MediaAssetMapper;
import com.kit.wmsbackend.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaAssetServiceImpl implements MediaAssetService {
    CloudinaryService cloudinaryService;
    CloudinaryProperties cloudinaryProperties;
    MediaAssetRepository mediaAssetRepository;
    ProductRepository productRepository;
    VariantRepository variantRepository;
    UserRepository userRepository;
    MediaAssetMapper mediaAssetMapper;

    @Override
    public List<MediaAssetResponse> listImages(MediaOwnerType ownerType, UUID ownerId) {
        validateOwnerExists(ownerType, ownerId);

        return mediaAssetRepository
                .findActiveByOwner(ownerType, ownerId, MediaResourceType.IMAGE)
                .stream()
                .map(mediaAssetMapper::toMediaAssetResponse)
                .toList();
    }

    @Override
    @Transactional
    public MediaAssetResponse uploadImage(
            MediaOwnerType ownerType,
            UUID ownerId,
            @Valid @NonNull MediaAssetUploadRequest request
    ) {
        validateOwnerExists(ownerType, ownerId);

        List<MediaAsset> existingAssets = mediaAssetRepository
                .findActiveByOwner(ownerType, ownerId, MediaResourceType.IMAGE);

        boolean shouldBePrimary = request.primaryRequested()
                || existingAssets.isEmpty()
                || existingAssets.stream().noneMatch(MediaAssetServiceImpl::isPrimary);

        if (shouldBePrimary) {
            demotePrimaryAssets(existingAssets);
        }

        CloudinaryUploadResponse uploadResponse = cloudinaryService.upload(
                new CloudinaryUploadRequest(
                        request.file(),
                        MediaResourceType.IMAGE,
                        request.publicId(),
                        buildOwnerFolder(ownerType, ownerId),
                        request.overwrite(),
                        buildOwnerImageTransformation(ownerType),
                        request.invalidate()
                )
        );

        if (StringUtils.hasText(request.publicId()) && Boolean.TRUE.equals(request.overwrite())) {
            mediaAssetRepository.hardDeleteByPublicId(request.publicId());
        }

        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setOwnerType(ownerType);
        mediaAsset.setOwnerId(ownerId);
        mediaAsset.setPublicId(uploadResponse.publicId());
        mediaAsset.setSecureUrl(uploadResponse.secureUrl());
        mediaAsset.setResourceType(uploadResponse.resourceType());
        mediaAsset.setFormat(uploadResponse.format());
        mediaAsset.setBytes(uploadResponse.bytes());
        mediaAsset.setWidth(uploadResponse.width());
        mediaAsset.setHeight(uploadResponse.height());
        mediaAsset.setOriginalFilename(uploadResponse.originalFilename());
        mediaAsset.setSortOrder(existingAssets.size());
        mediaAsset.setIsPrimary(shouldBePrimary);

        MediaAsset savedMediaAsset = mediaAssetRepository.save(mediaAsset);

        return mediaAssetMapper.toMediaAssetResponse(savedMediaAsset);
    }

    @Override
    @Transactional
    public MediaAssetResponse setPrimaryImage(MediaOwnerType ownerType, UUID ownerId, UUID imageId) {
        validateOwnerExists(ownerType, ownerId);

        List<MediaAsset> assets = mediaAssetRepository
                .findActiveByOwner(ownerType, ownerId, MediaResourceType.IMAGE);

        MediaAsset target = assets
                .stream()
                .filter(asset -> asset.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.MEDIA_ASSET_NOT_FOUND, imageId.toString()));

        if (!isPrimary(target)) {
            demotePrimaryAssets(assets);
            target.setIsPrimary(true);
        }

        return mediaAssetMapper.toMediaAssetResponse(target);
    }

    @Override
    @Transactional
    public void deleteImage(MediaOwnerType ownerType, UUID ownerId, UUID imageId) {
        deleteImages(ownerType, ownerId, Set.of(imageId));
    }

    @Override
    @Transactional
    public void bulkDeleteImages(MediaOwnerType ownerType, UUID ownerId, @NonNull Set<UUID> imageIds) {
        deleteImages(ownerType, ownerId, imageIds);
    }

    private void deleteImages(MediaOwnerType ownerType, UUID ownerId, @NonNull Set<UUID> imageIds) {
        validateOwnerExists(ownerType, ownerId);

        if (imageIds.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Image ids must not be empty");
        }

        Set<UUID> distinctImageIds = new LinkedHashSet<>(imageIds);

        List<MediaAsset> currentAssets = mediaAssetRepository
                .findActiveByOwner(ownerType, ownerId, MediaResourceType.IMAGE);

        Map<UUID, MediaAsset> currentAssetsById = currentAssets
                .stream()
                .collect(Collectors.toMap(MediaAsset::getId, Function.identity()));

        List<MediaAsset> assetsToDelete = distinctImageIds
                .stream()
                .map(currentAssetsById::get)
                .toList();

        if (assetsToDelete.stream().anyMatch(Objects::isNull) || assetsToDelete.size() != distinctImageIds.size()) {
            throw new AppException(ErrorCode.MEDIA_ASSET_NOT_FOUND, "one or more image ids are not found for owner: " + ownerId);
        }

        deleteFromCloudinary(ownerType, ownerId, assetsToDelete);

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("delete media assets");
        mediaAssetRepository.softDeleteAll(assetsToDelete, currentUserId);
        mediaAssetRepository.flush();

        List<MediaAsset> remainingAssets = currentAssets
                .stream()
                .filter(asset -> !distinctImageIds.contains(asset.getId()))
                .toList();

        ensurePrimaryAfterDelete(remainingAssets);
    }

    @Override
    public Map<UUID, String> findPrimaryImageUrls(MediaOwnerType ownerType, Collection<UUID> ownerIds) {
        if (ownerIds == null || ownerIds.isEmpty()) {
            return Map.of();
        }

        return mediaAssetRepository
                .findActivePrimaryByOwners(ownerType, ownerIds)
                .stream()
                .collect(Collectors.toMap(
                        MediaAsset::getOwnerId,
                        MediaAsset::getSecureUrl,
                        (existing, ignored) -> existing
                ));
    }

    private void validateOwnerExists(@NonNull MediaOwnerType ownerType, @NonNull UUID ownerId) {
        switch (ownerType) {
            case PRODUCT -> productRepository.findById(ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, ownerId.toString()));
            case VARIANT -> variantRepository.findById(ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND, ownerId.toString()));
            case USER -> userRepository.findById(ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ownerId.toString()));
        }
    }

    private void demotePrimaryAssets(@NonNull List<MediaAsset> assets) {
        List<MediaAsset> primaryAssets = assets
                .stream()
                .filter(MediaAssetServiceImpl::isPrimary)
                .toList();

        if (primaryAssets.isEmpty()) {
            return;
        }

        primaryAssets.forEach(asset -> asset.setIsPrimary(false));
        mediaAssetRepository.flush();
    }

    private void ensurePrimaryAfterDelete(@NonNull List<MediaAsset> remainingAssets) {
        if (remainingAssets.isEmpty() || remainingAssets.stream().anyMatch(MediaAssetServiceImpl::isPrimary)) {
            return;
        }

        remainingAssets
                .stream()
                .min(Comparator.comparing(MediaAsset::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(MediaAsset::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .ifPresent(asset -> asset.setIsPrimary(true));
    }

    private void deleteFromCloudinary(
            MediaOwnerType ownerType,
            UUID ownerId,
            @NonNull List<MediaAsset> assets
    ) {
        List<String> deletedPublicIds = new ArrayList<>();

        try {
            for (MediaAsset asset : assets) {
                CloudinaryDeleteResponse response = cloudinaryService.delete(asset.getPublicId(), MediaResourceType.IMAGE);
                if (!response.deleted()) {
                    throw new AppException(
                            ErrorCode.CLOUDINARY_DELETE_FAILED,
                            asset.getPublicId() + ": " + response.result()
                    );
                }
                deletedPublicIds.add(asset.getPublicId());
            }
        } catch (RuntimeException exception) {
            log.error(
                    "media_asset_cloudinary_delete_failed ownerType={} ownerId={} deletedPublicIds={}",
                    ownerType,
                    ownerId,
                    deletedPublicIds,
                    exception
            );
            throw exception;
        }
    }

    private @NonNull String buildOwnerFolder(@NonNull MediaOwnerType ownerType, @NonNull UUID ownerId) {
        String ownerFolder = ownerType.toString().toLowerCase(Locale.ROOT);
        return cloudinaryProperties.folder() + "/" + ownerFolder + "/" + ownerId;
    }

    private @NonNull String buildOwnerImageTransformation(@NonNull MediaOwnerType ownerType) {
        ImageOptimization optimization = switch (ownerType) {
            case PRODUCT -> cloudinaryProperties.productImage();
            case VARIANT -> cloudinaryProperties.variantImage();
            case USER -> cloudinaryProperties.userAvatar();
        };

        return "c_limit,h_" + optimization.maxHeight()
                + ",w_" + optimization.maxWidth()
                + "/q_" + optimization.quality();
    }

    private static boolean isPrimary(@NonNull MediaAsset asset) {
        return Boolean.TRUE.equals(asset.getIsPrimary());
    }
}
