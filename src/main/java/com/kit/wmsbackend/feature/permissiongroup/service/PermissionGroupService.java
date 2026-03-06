package com.kit.wmsbackend.feature.permissiongroup.service;

import com.kit.wmsbackend.entity.PermissionGroup;

import java.util.List;
import java.util.UUID;

public interface PermissionGroupService {
    List<PermissionGroup> findAll();

    PermissionGroup findById(UUID id);

    PermissionGroup create(PermissionGroup group);

    PermissionGroup update(UUID id, PermissionGroup group);

    void delete(UUID id);
}

