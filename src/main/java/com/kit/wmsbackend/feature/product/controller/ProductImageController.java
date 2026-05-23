package com.kit.wmsbackend.feature.product.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.MediaOwnerType;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.media.dto.MediaAssetBulkDeleteRequest;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import com.kit.wmsbackend.feature.media.dto.MediaAssetUploadRequest;
import com.kit.wmsbackend.feature.media.service.MediaAssetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageController {
    MediaAssetService mediaAssetService;

    @GetMapping
    @RequirePermission(PermissionCode.PRODUCT_READ)
    public ResponseEntity<ApiResponse<List<MediaAssetResponse>>> list(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(
                mediaAssetService.listImages(MediaOwnerType.PRODUCT, productId)
        ));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission(PermissionCode.PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<MediaAssetResponse>> upload(
            @PathVariable UUID productId,
            @RequestPart("file") @NotNull MultipartFile file,
            @RequestParam(required = false) Boolean isPrimary
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                mediaAssetService.uploadImage(
                        MediaOwnerType.PRODUCT,
                        productId,
                        new MediaAssetUploadRequest(file, null, isPrimary, false, false)
                )
        ));
    }

    @PatchMapping("/{imageId}/primary")
    @RequirePermission(PermissionCode.PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<MediaAssetResponse>> setPrimary(
            @PathVariable UUID productId,
            @PathVariable UUID imageId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                mediaAssetService.setPrimaryImage(MediaOwnerType.PRODUCT, productId, imageId)
        ));
    }

    @DeleteMapping("/{imageId}")
    @RequirePermission(PermissionCode.PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID productId,
            @PathVariable UUID imageId
    ) {
        mediaAssetService.deleteImage(MediaOwnerType.PRODUCT, productId, imageId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    @RequirePermission(PermissionCode.PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<Void>> bulkDelete(
            @PathVariable UUID productId,
            @RequestBody @Valid @NonNull MediaAssetBulkDeleteRequest request
    ) {
        mediaAssetService.bulkDeleteImages(MediaOwnerType.PRODUCT, productId, request.imageIds());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
