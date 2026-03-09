package com.kit.wmsbackend.feature.permissiongroup.repository;

import com.kit.wmsbackend.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {
    Optional<PermissionGroup> findByCode(String code);

    boolean existsByCode(String code);
}

