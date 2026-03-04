package com.kit.wmsbackend.feature.permission.repository;

import com.kit.wmsbackend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    List<Permission> findAllByGroupId(String groupId);
}

