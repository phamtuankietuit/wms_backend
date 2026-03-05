package com.kit.wmsbackend.feature.role.service;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.feature.role.dto.RoleResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    Role findById(String id);

    Role create(Role role);

    Role update(String id, Role role);

    void delete(String id);
}

