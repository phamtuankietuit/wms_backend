package com.kit.wmsbackend.feature.user.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.user.dto.*;
import com.kit.wmsbackend.feature.user.service.UserService;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    @PutMapping("/{id}/roles")
    @RequirePermission(PermissionCode.USER_ROLE_UPDATE)
    public ResponseEntity<ApiResponse<UserResponse>> updateRoles(
            @PathVariable UUID id,
            @Valid @RequestBody Collection<UUID> ids
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateRoles(id, ids)));
    }

    @PutMapping("/{id}/warehouses")
    @RequirePermission(PermissionCode.USER_WAREHOUSE_UPDATE)
    public ResponseEntity<ApiResponse<List<UserWarehouseResponse>>> updateWarehouses(
            @PathVariable UUID id,
            @Valid @RequestBody Collection<UUID> ids
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateWarehouses(id, ids)));
    }

    @GetMapping("/{id}/warehouses")
    @RequirePermission(PermissionCode.USER_READ)
    public ResponseEntity<ApiResponse<List<UserWarehouseResponse>>> getWarehouses(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getWarehouses(id)));
    }

    @PatchMapping("/activate")
    @RequirePermission(PermissionCode.USER_UPDATE)
    public ResponseEntity<ApiResponse<List<UserResponse>>> activate(
            @Valid @RequestBody @NotNull @NotEmpty Collection<UUID> ids
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.activate(ids)));
    }

    @PatchMapping("/disabled")
    @RequirePermission(PermissionCode.USER_UPDATE)
    public ResponseEntity<ApiResponse<List<UserResponse>>> disabled(
            @Valid @RequestBody @NotNull @NotEmpty Collection<UUID> ids
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.disabled(ids)));
    }
}

