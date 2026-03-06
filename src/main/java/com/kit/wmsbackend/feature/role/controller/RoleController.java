package com.kit.wmsbackend.feature.role.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.security.PermissionCode;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.feature.role.service.RoleService;
import com.kit.wmsbackend.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @RequirePermission(PermissionCode.ROLE_READ)
    public ResponseEntity<ApiResponse<List<Role>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAll()));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_READ)
    public ResponseEntity<ApiResponse<Role>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    @PostMapping
    @RequirePermission(PermissionCode.ROLE_CREATE)
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(roleService.create(role)));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_UPDATE)
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable UUID id, @RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, role)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

