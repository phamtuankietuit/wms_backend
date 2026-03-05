package com.kit.wmsbackend.feature.permission.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.entity.Permission;
import com.kit.wmsbackend.security.PermissionCode;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.feature.permission.service.PermissionService;
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

@ApiPrefix
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @RequirePermission(PermissionCode.PERMISSION_READ)
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_READ)
    public ResponseEntity<Permission> findById(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @PostMapping
    @RequirePermission(PermissionCode.PERMISSION_CREATE)
    public ResponseEntity<Permission> create(@RequestBody Permission permission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(permission));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_UPDATE)
    public ResponseEntity<Permission> update(@PathVariable String id, @RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.update(id, permission));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

