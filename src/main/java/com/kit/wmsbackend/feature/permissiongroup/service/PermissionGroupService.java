package com.kit.wmsbackend.feature.permissiongroup.service;

import com.kit.wmsbackend.entity.PermissionGroup;

import java.util.List;

public interface PermissionGroupService {
    List<PermissionGroup> findAll();

    PermissionGroup findById(String id);

    PermissionGroup create(PermissionGroup group);

    PermissionGroup update(String id, PermissionGroup group);

    void delete(String id);
}

