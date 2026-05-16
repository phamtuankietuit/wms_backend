package com.kit.wmsbackend.feature.permissiongroup.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.permissiongroup.dto.PermissionGroupResponse;
import com.kit.wmsbackend.feature.permissiongroup.service.PermissionGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/permission-groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionGroupController {
    PermissionGroupService permissionGroupService;

    @PostMapping("/list")
    @RequirePermission(PermissionCode.PERMISSION_GROUP_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<PermissionGroupResponse>>>> list(
            @RequestBody @Valid ListRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(permissionGroupService.list(request)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_GROUP_READ)
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(permissionGroupService.getById(id)));
    }
}

