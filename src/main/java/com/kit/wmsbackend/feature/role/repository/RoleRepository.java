package com.kit.wmsbackend.feature.role.repository;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.repository.BaseAuditRepository;

public interface RoleRepository extends BaseAuditRepository<Role> {
    boolean existsByCode(String code);
}

