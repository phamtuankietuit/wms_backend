package com.kit.wmsbackend.feature.permission.repository;

import com.kit.wmsbackend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    List<Permission> findAllByGroupId(UUID groupId);
}

