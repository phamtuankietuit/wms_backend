package com.kit.wmsbackend.feature.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.kit.wmsbackend.config.properties.CloudinaryProperties;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.MediaResourceType;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.media.dto.CloudinaryDeleteResponse;
import com.kit.wmsbackend.feature.media.dto.CloudinaryUploadResponse;
import com.kit.wmsbackend.validator.CloudinaryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceImplTest {
    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() {
        CloudinaryProperties properties = new CloudinaryProperties(
                "demo",
                "key",
                "secret",
                true,
                "wms",
                10_000L,
                List.of("image/png"),
                List.of("video/mp4"),
                List.of("application/pdf")
        );
        CloudinaryValidator validator = new CloudinaryValidator(properties);
        cloudinaryService = new CloudinaryServiceImpl(cloudinary, properties, validator);
    }

    @Test
    void uploadMapsCloudinaryResponse() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "product.png",
                "image/png",
                "png".getBytes()
        );
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", "wms/product");
        uploadResult.put("secure_url", "https://res.cloudinary.com/demo/image/upload/wms/product.png");
        uploadResult.put("resource_type", "image");
        uploadResult.put("format", "png");
        uploadResult.put("bytes", 123L);
        uploadResult.put("width", 100);
        uploadResult.put("height", 80);
        uploadResult.put("original_filename", "product");

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(uploadResult);

        CloudinaryUploadResponse response = cloudinaryService.upload(file, MediaResourceType.IMAGE);

        assertEquals("wms/product", response.publicId());
        assertEquals("https://res.cloudinary.com/demo/image/upload/wms/product.png", response.secureUrl());
        assertEquals(MediaResourceType.IMAGE, response.resourceType());
        assertEquals("png", response.format());
        assertEquals(123L, response.bytes());
        assertEquals(100, response.width());
        assertEquals(80, response.height());
        assertEquals("product", response.originalFilename());
    }

    @Test
    void uploadWrapsCloudinaryFailure() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "product.png",
                "image/png",
                "png".getBytes()
        );

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenThrow(new IOException("network"));

        AppException exception = assertThrows(
                AppException.class,
                () -> cloudinaryService.upload(file, MediaResourceType.IMAGE)
        );

        assertEquals(ErrorCode.CLOUDINARY_UPLOAD_FAILED, exception.getErrorCode());
    }

    @Test
    void deleteMapsCloudinaryResponse() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(), anyMap())).thenReturn(Map.of("result", "ok"));

        CloudinaryDeleteResponse response = cloudinaryService.delete("wms/product", MediaResourceType.IMAGE);

        assertEquals("wms/product", response.publicId());
        assertEquals(MediaResourceType.IMAGE, response.resourceType());
        assertTrue(response.deleted());
        assertEquals("ok", response.result());
    }
}
