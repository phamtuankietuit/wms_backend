package com.kit.wmsbackend.feature.user.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.user.dto.UserCreateRequest;
import com.kit.wmsbackend.feature.user.dto.UserDeletedResponse;
import com.kit.wmsbackend.feature.user.dto.UserInfoUpdateRequest;
import com.kit.wmsbackend.feature.user.dto.UserResponse;
import com.kit.wmsbackend.feature.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    @RequirePermission(PermissionCode.USER_CREATE)
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody UserCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.create(request)));
    }

    @PostMapping("/list")
    @RequirePermission(PermissionCode.USER_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<UserResponse>>>> list(
            @Valid @RequestBody ListRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.list(request)));
    }

    @PostMapping("/list/deleted")
    @RequirePermission(PermissionCode.USER_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<UserDeletedResponse>>>> listDeleted(
            @Valid @RequestBody ListRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.listDeleted(request)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.USER_READ)
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.USER_DELETE)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    @RequirePermission(PermissionCode.USER_DELETE)
    public ResponseEntity<ApiResponse<Void>> bulkDelete(
            @RequestBody @Valid @NotNull Collection<UUID> ids
    ) {
        userService.bulkDelete(ids);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/restore")
    @RequirePermission(PermissionCode.USER_RESTORE)
    public ResponseEntity<ApiResponse<UserResponse>> restore(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.restore(id)));
    }

    @PatchMapping("/restore")
    @RequirePermission(PermissionCode.USER_RESTORE)
    public ResponseEntity<ApiResponse<List<UserResponse>>> restore(@RequestBody @Valid @NotNull Collection<UUID> ids) {
        return ResponseEntity.ok(ApiResponse.success(userService.bulkRestore(ids)));
    }

    @PatchMapping("/{id}")
    @RequirePermission(PermissionCode.USER_UPDATE)
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(
            @PathVariable UUID id,
            @Valid @RequestBody UserInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateInfo(id, request)));
    }
}

