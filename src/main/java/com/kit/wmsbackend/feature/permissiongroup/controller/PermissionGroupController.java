package com.kit.wmsbackend.feature.permissiongroup.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.security.PermissionCode;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.feature.permissiongroup.service.PermissionGroupService;
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
@RequestMapping("/permission-groups")
@RequiredArgsConstructor
public class PermissionGroupController {
    private final PermissionGroupService permissionGroupService;

    @GetMapping
    @RequirePermission(PermissionCode.PERMISSION_GROUP_READ)
    public ResponseEntity<List<PermissionGroup>> findAll() {
        return ResponseEntity.ok(permissionGroupService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_GROUP_READ)
    public ResponseEntity<PermissionGroup> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionGroupService.findById(id));
    }

    @PostMapping
    @RequirePermission(PermissionCode.PERMISSION_GROUP_CREATE)
    public ResponseEntity<PermissionGroup> create(@RequestBody PermissionGroup group) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionGroupService.create(group));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_GROUP_UPDATE)
    public ResponseEntity<PermissionGroup> update(@PathVariable UUID id, @RequestBody PermissionGroup group) {
        return ResponseEntity.ok(permissionGroupService.update(id, group));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.PERMISSION_GROUP_DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        permissionGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

