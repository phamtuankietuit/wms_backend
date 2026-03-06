package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.entity.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<Role> findAll();

    Role findById(UUID id);

    Role create(Role role);

    Role update(UUID id, Role role);

    void delete(UUID id);
}

