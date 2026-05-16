package com.kit.wmsbackend.feature.role.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.role.dto.RoleRequest;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import com.kit.wmsbackend.feature.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

