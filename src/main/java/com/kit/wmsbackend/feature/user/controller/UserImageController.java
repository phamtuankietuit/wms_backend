package com.kit.wmsbackend.feature.user.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import com.kit.wmsbackend.feature.user.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/users/{userId}/avatar")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserImageController {
    UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission(PermissionCode.USER_PROFILE_UPDATE)
    public ResponseEntity<ApiResponse<MediaAssetResponse>> upload(
            @PathVariable UUID userId,
            @RequestPart("file") @NotNull MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success(
               userService.uploadAvatar(userId, file)
        ));
    }
}
