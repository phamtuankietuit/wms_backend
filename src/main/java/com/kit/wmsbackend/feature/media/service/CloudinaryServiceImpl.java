package com.kit.wmsbackend.feature.media.service;

import com.cloudinary.Cloudinary;
import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.media.dto.CloudinaryDeleteResponse;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadRequest;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadResponse;
import com.kit.wmsbackend.validator.CloudinaryValidator;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements CloudinaryService {
    Cloudinary cloudinary;
    CloudinaryProperties properties;
    CloudinaryValidator cloudinaryValidator;

    @Override
    public CloudinaryUploadResponse upload(MultipartFile file, MediaResourceType resourceType) {
        return upload(new CloudinaryUploadRequest(file, resourceType, null, null, null));
    }

    @Override
    public CloudinaryUploadResponse upload(@Valid @NonNull CloudinaryUploadRequest request) {
        cloudinaryValidator.validateUpload(request.file(), request.resourceType());

        String folder = resolveFolder(request.folder());
        cloudinaryValidator.validateFolder(folder);
        cloudinaryValidator.validatePublicId(request.publicId(), request.resourceType());

        Map<String, Object> options = buildUploadOptions(request, folder);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(request.file().getBytes(), options);
            return toUploadResponse(result, request.resourceType(), request.file());
        } catch (IOException exception) {
            log.error(
                    "cloudinary_upload_failed resourceType={} filename={}",
                    request.resourceType(),
                    safeFilename(request.file()),
                    exception
            );
            throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED, exception.getMessage());
        } catch (RuntimeException exception) {
            if (exception instanceof AppException appException) {
                throw appException;
            }

            log.error(
                    "cloudinary_upload_failed resourceType={} filename={}",
                    request.resourceType(),
                    safeFilename(request.file()),
                    exception
            );
            throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED, exception.getMessage());
        }
    }

    @Override
    public CloudinaryDeleteResponse delete(String publicId, MediaResourceType resourceType) {
        if (!StringUtils.hasText(publicId)) {
            throw new AppException(ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID, "public id must not be blank");
        }

        cloudinaryValidator.validatePublicId(publicId, resourceType);

        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", resourceType.getValue());

        String normalizedPublicId = publicId.trim();

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(normalizedPublicId, options);
            String resultValue = asString(result.get("result"));
            boolean deleted = "ok".equalsIgnoreCase(resultValue);
            return new CloudinaryDeleteResponse(normalizedPublicId, resourceType, deleted, resultValue);
        } catch (IOException exception) {
            log.error("cloudinary_delete_failed resourceType={} publicId={}", resourceType, normalizedPublicId, exception);
            throw new AppException(ErrorCode.CLOUDINARY_DELETE_FAILED, exception.getMessage());
        } catch (RuntimeException exception) {
            if (exception instanceof AppException appException) {
                throw appException;
            }

            log.error("cloudinary_delete_failed resourceType={} publicId={}", resourceType, normalizedPublicId, exception);
            throw new AppException(ErrorCode.CLOUDINARY_DELETE_FAILED, exception.getMessage());
        }
    }

    private @NonNull Map<String, Object> buildUploadOptions(@NonNull CloudinaryUploadRequest request, String folder) {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", request.resourceType().getValue());
        options.put("folder", folder);
        options.put("overwrite", request.shouldOverwrite());
        options.put("use_filename", true);
        options.put("unique_filename", true);

        if (StringUtils.hasText(request.publicId())) {
            options.put("public_id", request.publicId().trim());
            options.put("unique_filename", false);
        } else if (request.resourceType() == MediaResourceType.RAW) {
            options.put("public_id", buildRawPublicId(request.file().getOriginalFilename()));
            options.put("unique_filename", false);
        }

        return options;
    }

    private CloudinaryUploadResponse toUploadResponse(
            @NonNull Map<?, ?> result,
            MediaResourceType requestedResourceType,
            MultipartFile file
    ) {
        String publicId = asString(result.get("public_id"));
        String secureUrl = asString(result.get("secure_url"));

        if (!StringUtils.hasText(publicId) || !StringUtils.hasText(secureUrl)) {
            throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED, "missing public_id or secure_url in response");
        }

        return new CloudinaryUploadResponse(
                publicId,
                secureUrl,
                resolveResourceType(result, requestedResourceType),
                asString(result.get("format")),
                asLong(result.get("bytes")),
                asInteger(result.get("width")),
                asInteger(result.get("height")),
                resolveOriginalFilename(result, file)
        );
    }

    private String resolveFolder(String folder) {
        return StringUtils.hasText(folder) ? folder.trim() : properties.folder();
    }

    private MediaResourceType resolveResourceType(Map<?, ?> result, MediaResourceType requestedResourceType) {
        String resourceType = asString(result.get("resource_type"));
        if (!StringUtils.hasText(resourceType)) {
            return requestedResourceType;
        }

        return MediaResourceType.fromValue(resourceType.toLowerCase(Locale.ROOT));
    }

    private String resolveOriginalFilename(Map<?, ?> result, MultipartFile file) {
        String originalFilename = asString(result.get("original_filename"));
        if (StringUtils.hasText(originalFilename)) {
            return originalFilename;
        }

        return safeFilename(file);
    }

    private @NonNull String buildRawPublicId(String originalFilename) {
        String filename = originalFilename == null ? "file" : originalFilename.trim();
        int extensionStart = filename.lastIndexOf('.');
        String baseName = extensionStart > 0 ? filename.substring(0, extensionStart) : filename;
        String extension = extensionStart > 0 ? filename.substring(extensionStart).toLowerCase(Locale.ROOT) : "";

        String normalizedBaseName = baseName
                .replaceAll("[^A-Za-z0-9_-]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");

        if (!StringUtils.hasText(normalizedBaseName)) {
            normalizedBaseName = "file";
        }

        return normalizedBaseName + "-" + UUID.randomUUID() + extension;
    }

    private String safeFilename(MultipartFile file) {
        if (file == null || !StringUtils.hasText(file.getOriginalFilename())) {
            return "unknown";
        }

        return file.getOriginalFilename();
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }
}
