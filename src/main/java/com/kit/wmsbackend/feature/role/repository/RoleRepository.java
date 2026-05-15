package com.kit.wmsbackend.feature.role.repository;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.repository.BaseAuditRepository;

import java.util.Optional;

public interface RoleRepository extends BaseAuditRepository<Role> {
    Optional<Role> findByName(String name);
}

