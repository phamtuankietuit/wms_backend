package com.kit.wmsbackend.feature.role.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.role.dto.*;
import com.kit.wmsbackend.feature.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class RoleController {
    RoleService roleService;

    @PostMapping
    @RequirePermission(PermissionCode.ROLE_CREATE)
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @RequestBody @Valid RoleRequest roleRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleService.create(roleRequest)));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_UPDATE)
    public ResponseEntity<ApiResponse<RoleUpdateResponse>> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid RoleUpdateRequest roleUpdateRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, roleUpdateRequest)));
    }

    @PostMapping("/list")
    @RequirePermission(PermissionCode.ROLE_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<RoleListResponse>>>> list(
            @RequestBody @Valid ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleService.list(listRequest)));
    }
}

