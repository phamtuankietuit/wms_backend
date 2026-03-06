package com.kit.wmsbackend.feature.permission.service;

import com.kit.wmsbackend.entity.Permission;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    List<Permission> findAll();

    Permission findById(UUID id);

    Permission create(Permission permission);

    Permission update(UUID id, Permission permission);

    void delete(UUID id);
}

