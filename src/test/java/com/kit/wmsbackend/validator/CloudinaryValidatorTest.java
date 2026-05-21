package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CloudinaryValidatorTest {
    private CloudinaryValidator validator;

    @BeforeEach
    void setUp() {
        CloudinaryProperties properties = new CloudinaryProperties(
                "demo",
                "key",
                "secret",
                true,
                "wms",
                10L,
                List.of("image/png"),
                List.of("video/mp4"),
                List.of("application/pdf", "text/csv")
        );
        validator = new CloudinaryValidator(properties);
    }

    @Test
    void validateUploadAcceptsAllowedRawFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invoice.pdf",
                "application/pdf",
                "pdf".getBytes()
        );

        assertDoesNotThrow(() -> validator.validateUpload(file, MediaResourceType.RAW));
    }

    @Test
    void validateUploadRejectsTooLargeFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invoice.pdf",
                "application/pdf",
                "this file is too large".getBytes()
        );

        AppException exception = assertThrows(
                AppException.class,
                () -> validator.validateUpload(file, MediaResourceType.RAW)
        );

        assertEquals(ErrorCode.CLOUDINARY_FILE_TOO_LARGE, exception.getErrorCode());
    }

    @Test
    void validateUploadRejectsDisallowedContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.html",
                "text/html",
                "html".getBytes()
        );

        AppException exception = assertThrows(
                AppException.class,
                () -> validator.validateUpload(file, MediaResourceType.RAW)
        );

        assertEquals(ErrorCode.CLOUDINARY_CONTENT_TYPE_NOT_ALLOWED, exception.getErrorCode());
    }

    @Test
    void validatePublicIdRejectsRawPublicIdWithoutExtension() {
        AppException exception = assertThrows(
                AppException.class,
                () -> validator.validatePublicId("documents/invoice", MediaResourceType.RAW)
        );

        assertEquals(ErrorCode.CLOUDINARY_PUBLIC_ID_INVALID, exception.getErrorCode());
    }

    @Test
    void validateFolderRejectsPathTraversal() {
        AppException exception = assertThrows(
                AppException.class,
                () -> validator.validateFolder("wms/../secret")
        );

        assertEquals(ErrorCode.CLOUDINARY_FOLDER_INVALID, exception.getErrorCode());
    }
}
