package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryValidator {
    static final int MAX_IDENTIFIER_LENGTH = 255;
    static final Pattern CLOUDINARY_PATH_PATTERN = Pattern.compile("^[A-Za-z0-9_./-]+$");
    static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[^\\\\/\\p{Cntrl}]+$");

    CloudinaryProperties properties;

    public void validateUpload(MultipartFile file, MediaResourceType resourceType) {
        validateResourceType(resourceType);

        if (file == null) {
            throw new AppException(ErrorCode.CLOUDINARY_FILE_REQUIRED);
        }

        if (file.isEmpty()) {
            throw new AppException(ErrorCode.CLOUDINARY_FILE_EMPTY);
        }

        if (file.getSize() > properties.maxFileSizeBytes()) {
            throw new AppException(
                    ErrorCode.CLOUDINARY_FILE_TOO_LARGE,
                    file.getSize() + " bytes, max " + properties.maxFileSizeBytes() + " bytes"
            );
        }

        validateContentType(file.getContentType(), resourceType);
        validateOriginalFilename(file.getOriginalFilename(), resourceType);
    }

    public void validatePublicId(String publicId, MediaResourceType resourceType) {
        validateResourceType(resourceType);

        if (!StringUtils.hasText(publicId)) {
            return;
        }

        validateCloudinaryPath(publicId, ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID);

        if (resourceType == MediaResourceType.RAW && isMissingExtension(publicId)) {
            throw new AppException(
                    ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID,
                    "raw public id must include a file extension"
            );
        }
    }

    public void validateFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            throw new AppException(ErrorCode.CLOUDINARY_FOLDER_INVALID, "folder must not be blank");
        }

        validateCloudinaryPath(folder, ErrorCode.CLOUDINARY_FOLDER_INVALID);
    }

    private void validateResourceType(MediaResourceType resourceType) {
        if (resourceType == null) {
            throw new AppException(ErrorCode.CLOUDINARY_RESOURCE_TYPE_REQUIRED);
        }
    }

    private void validateContentType(String contentType, @NonNull MediaResourceType resourceType) {
        if (!StringUtils.hasText(contentType)) {
            throw new AppException(ErrorCode.CLOUDINARY_CONTENT_TYPE_NOT_ALLOWED, "blank");
        }

        String normalizedContentType = contentType.toLowerCase(Locale.ROOT);
        boolean allowed = allowedContentTypes(resourceType)
                .stream()
                .map(allowedContentType -> allowedContentType.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedContentType::equals);

        if (!allowed) {
            throw new AppException(ErrorCode.CLOUDINARY_CONTENT_TYPE_NOT_ALLOWED, contentType);
        }
    }

    private void validateOriginalFilename(String originalFilename, MediaResourceType resourceType) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new AppException(ErrorCode.CLOUDINARY_ORIGINAL_FILENAME_INVALID, "blank");
        }

        String filename = originalFilename.trim();
        if (filename.length() > MAX_IDENTIFIER_LENGTH) {
            throw new AppException(ErrorCode.CLOUDINARY_ORIGINAL_FILENAME_INVALID, "too long");
        }

        if (filename.contains("..") || !SAFE_FILENAME_PATTERN.matcher(filename).matches()) {
            throw new AppException(ErrorCode.CLOUDINARY_ORIGINAL_FILENAME_INVALID, originalFilename);
        }

        if (resourceType == MediaResourceType.RAW && isMissingExtension(filename)) {
            throw new AppException(
                    ErrorCode.CLOUDINARY_ORIGINAL_FILENAME_INVALID,
                    "raw files must include a file extension"
            );
        }
    }

    private void validateCloudinaryPath(@NonNull String value, ErrorCode errorCode) {
        String normalized = value.trim();

        if (normalized.length() > MAX_IDENTIFIER_LENGTH) {
            throw new AppException(errorCode, "too long");
        }

        if (normalized.startsWith("/") || normalized.endsWith("/") || normalized.contains("..")
                || normalized.contains("//")
                || normalized.contains("\\") || !CLOUDINARY_PATH_PATTERN.matcher(normalized).matches()) {
            throw new AppException(errorCode, value);
        }
    }

    private List<String> allowedContentTypes(@NonNull MediaResourceType resourceType) {
        return switch (resourceType) {
            case IMAGE -> properties.allowedImageContentTypes();
            case VIDEO -> properties.allowedVideoContentTypes();
            case RAW -> properties.allowedRawContentTypes();
        };
    }

    private boolean isMissingExtension(@NonNull String filename) {
        String name = filename.substring(filename.lastIndexOf('/') + 1);
        int extensionStart = name.lastIndexOf('.');
        return extensionStart <= 0 || extensionStart >= name.length() - 1;
    }
}
