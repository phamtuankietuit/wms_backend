package com.kit.wmsbackend.feature.permissiongroup.controller;

import com.kit.wmsbackend.entity.PermissionGroup;
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

@RestController
@RequestMapping("/api/permission-groups")
@RequiredArgsConstructor
public class PermissionGroupController {
    private final PermissionGroupService permissionGroupService;

    @GetMapping
    public ResponseEntity<List<PermissionGroup>> findAll() {
        return ResponseEntity.ok(permissionGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionGroup> findById(@PathVariable String id) {
        return ResponseEntity.ok(permissionGroupService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PermissionGroup> create(@RequestBody PermissionGroup group) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionGroupService.create(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionGroup> update(@PathVariable String id, @RequestBody PermissionGroup group) {
        return ResponseEntity.ok(permissionGroupService.update(id, group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        permissionGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

