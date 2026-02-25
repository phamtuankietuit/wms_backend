package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<PermissionGroup> findByCode(String code);

    boolean existsByCode(String code);
}
