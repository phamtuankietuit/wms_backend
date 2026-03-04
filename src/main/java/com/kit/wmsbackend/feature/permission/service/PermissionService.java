package com.kit.wmsbackend.feature.permission.service;

import com.kit.wmsbackend.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> findAll();

    Permission findById(String id);

    Permission create(Permission permission);

    Permission update(String id, Permission permission);

    void delete(String id);
}

