package com.kit.wmsbackend.service;

import com.kit.wmsbackend.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    Role findById(String id);

    Role create(Role role);

    Role update(String id, Role role);

    void delete(String id);
}
