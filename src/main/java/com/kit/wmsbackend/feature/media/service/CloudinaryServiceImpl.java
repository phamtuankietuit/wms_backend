package com.kit.wmsbackend.feature.media.service;

import com.cloudinary.Cloudinary;
import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.media.constant.CloudinaryAttribute;
import com.kit.wmsbackend.feature.media.dto.CloudinaryDeleteResponse;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadRequest;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadResponse;
import com.kit.wmsbackend.validator.CloudinaryValidator;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements CloudinaryService {
    static final int DELETE_BATCH_SIZE = 100;
    static final String DEFAULT_DELIVERY_TYPE = "upload";
    static final String DELETE_STATUS_DELETED = "deleted";

    Cloudinary cloudinary;
    CloudinaryProperties properties;
    CloudinaryValidator cloudinaryValidator;

    @Override
    public CloudinaryUploadResponse upload(@NonNull CloudinaryUploadRequest request) {
        cloudinaryValidator.validateUpload(request.file(), request.resourceType());

        String folder = resolveFolder(request.folder());
        cloudinaryValidator.validateFolder(folder);
        cloudinaryValidator.validatePublicId(request.publicId(), request.resourceType());
        cloudinaryValidator.validateTransformation(request.transformation());

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
    public CloudinaryDeleteResponse delete(@NonNull String publicId, MediaResourceType resourceType) {
        return deleteAll(List.of(publicId), resourceType).getFirst();
    }

    @Override
    public List<CloudinaryDeleteResponse> deleteAll(
            @NonNull Collection<String> publicIds,
            MediaResourceType resourceType
    ) {
        List<String> normalizedPublicIds = normalizePublicIds(publicIds, resourceType);
        List<CloudinaryDeleteResponse> responses = new ArrayList<>(normalizedPublicIds.size());

        for (int start = 0; start < normalizedPublicIds.size(); start += DELETE_BATCH_SIZE) {
            int end = Math.min(start + DELETE_BATCH_SIZE, normalizedPublicIds.size());
            deleteBatch(normalizedPublicIds.subList(start, end), resourceType, responses);
        }

        return responses;
    }

    private @NonNull Map<String, Object> buildUploadOptions(@NonNull CloudinaryUploadRequest request, String folder) {
        Map<String, Object> options = new HashMap<>();
        options.put(CloudinaryAttribute.RESOURCE_TYPE.getKey(), request.resourceType().getValue());
        options.put(CloudinaryAttribute.FOLDER.getKey(), folder);
        options.put(CloudinaryAttribute.OVERWRITE.getKey(), request.shouldOverwrite());
        options.put(CloudinaryAttribute.INVALIDATE.getKey(), request.shouldInvalidate());

        if (StringUtils.hasText(request.publicId())) {
            options.put(CloudinaryAttribute.PUBLIC_ID.getKey(), normalizePublicIdForUpload(request, folder));
        }

        if (StringUtils.hasText(request.transformation())) {
            options.put(CloudinaryAttribute.TRANSFORMATION.getKey(), request.transformation().trim());
        }

        return options;
    }

    private @NonNull List<String> normalizePublicIds(
            Collection<String> publicIds,
            MediaResourceType resourceType
    ) {
        cloudinaryValidator.validatePublicId(null, resourceType);

        if (publicIds == null || publicIds.isEmpty()) {
            throw new AppException(ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID, "public ids must not be empty");
        }

        Set<String> uniquePublicIds = new LinkedHashSet<>();

        for (String publicId : publicIds) {
            if (!StringUtils.hasText(publicId)) {
                throw new AppException(ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID, "public id must not be blank");
            }

            String normalizedPublicId = publicId.trim();
            cloudinaryValidator.validatePublicId(normalizedPublicId, resourceType);
            uniquePublicIds.add(normalizedPublicId);
        }

        return new ArrayList<>(uniquePublicIds);
    }

    private void deleteBatch(
            @NonNull List<String> publicIds,
            MediaResourceType resourceType,
            @NonNull List<CloudinaryDeleteResponse> responses
    ) {
        Map<String, Object> options = buildDeleteOptions(resourceType);

        try {
            Map<?, ?> result = cloudinary.api().deleteResources(publicIds, options);
            Map<?, ?> deletedResults = asMap(result == null ? null : result.get(CloudinaryAttribute.DELETED.getKey()));

            for (String publicId : publicIds) {
                String resultValue = asString(deletedResults.get(publicId));
                responses.add(new CloudinaryDeleteResponse(
                        publicId,
                        resourceType,
                        DELETE_STATUS_DELETED.equalsIgnoreCase(resultValue),
                        resultValue
                ));
            }
        } catch (Exception exception) {
            if (exception instanceof AppException appException) {
                throw appException;
            }

            List<String> deletedPublicIds = responses
                    .stream()
                    .filter(CloudinaryDeleteResponse::deleted)
                    .map(CloudinaryDeleteResponse::publicId)
                    .toList();

            log.error(
                    "cloudinary_bulk_delete_failed resourceType={} batchSize={} publicIds={} deletedPublicIds={}",
                    resourceType,
                    publicIds.size(),
                    publicIds,
                    deletedPublicIds,
                    exception
            );
            throw new AppException(ErrorCode.CLOUDINARY_DELETE_FAILED, exception.getMessage());
        }
    }

    private @NonNull Map<String, Object> buildDeleteOptions(@NonNull MediaResourceType resourceType) {
        Map<String, Object> options = new HashMap<>();
        options.put(CloudinaryAttribute.RESOURCE_TYPE.getKey(), resourceType.getValue());
        options.put(CloudinaryAttribute.TYPE.getKey(), DEFAULT_DELIVERY_TYPE);
        return options;
    }

    private @NonNull String normalizePublicIdForUpload(
            @NonNull CloudinaryUploadRequest request,
            @NonNull String folder
    ) {
        String publicId = request.publicId().trim();

        if (!request.shouldOverwrite()) {
            return publicId;
        }

        String normalizedPublicId = publicId.replace('\\', '/');
        String normalizedFolder = folder.trim().replace('\\', '/');

        if (normalizedPublicId.startsWith(normalizedFolder + "/")) {
            return normalizedPublicId.substring(normalizedFolder.length() + 1);
        }

        if (normalizedPublicId.startsWith(properties.folder() + "/")) {
            int filenameStart = normalizedPublicId.lastIndexOf('/');
            return filenameStart >= 0 ? normalizedPublicId.substring(filenameStart + 1) : normalizedPublicId;
        }

        return normalizedPublicId;
    }

    private @NonNull CloudinaryUploadResponse toUploadResponse(
            @NonNull Map<?, ?> result,
            MediaResourceType requestedResourceType,
            MultipartFile file
    ) {
        String publicId = asString(result.get(CloudinaryAttribute.PUBLIC_ID.getKey()));
        String secureUrl = asString(result.get(CloudinaryAttribute.SECURE_URL.getKey()));

        if (!StringUtils.hasText(publicId) || !StringUtils.hasText(secureUrl)) {
            throw new AppException(
                    ErrorCode.CLOUDINARY_UPLOAD_FAILED,
                    "missing " + CloudinaryAttribute.PUBLIC_ID.getKey()
                            + " or " + CloudinaryAttribute.SECURE_URL.getKey() + " in response"
            );
        }

        return new CloudinaryUploadResponse(
                publicId,
                secureUrl,
                resolveResourceType(result, requestedResourceType),
                asString(result.get(CloudinaryAttribute.FORMAT.getKey())),
                asLong(result.get(CloudinaryAttribute.BYTES.getKey())),
                asInteger(result.get(CloudinaryAttribute.WIDTH.getKey())),
                asInteger(result.get(CloudinaryAttribute.HEIGHT.getKey())),
                safeFilename(file)
        );
    }

    private String resolveFolder(String folder) {
        return StringUtils.hasText(folder) ? folder.trim() : properties.folder();
    }

    private MediaResourceType resolveResourceType(@NonNull Map<?, ?> result, MediaResourceType requestedResourceType) {
        String resourceType = asString(result.get(CloudinaryAttribute.RESOURCE_TYPE.getKey()));
        if (!StringUtils.hasText(resourceType)) {
            return requestedResourceType;
        }

        return MediaResourceType.fromValue(resourceType.toLowerCase(Locale.ROOT));
    }

    private String safeFilename(MultipartFile file) {
        if (file == null) {
            return "unknown";
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            return "unknown";
        }

        String normalizedFilename = originalFilename.trim().replace('\\', '/');
        String filename = StringUtils.getFilename(normalizedFilename);
        return StringUtils.hasText(filename) ? filename : normalizedFilename;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Map<?, ?> asMap(Object value) {
        return value instanceof Map<?, ?> map ? map : Map.of();
    }

    private Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }
}
