package com.kit.wmsbackend.feature.role.repository;

import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends BaseAuditRepository<Role> {
    boolean existsByCode(String code);

    @Query("""
        SELECT r
        FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.id = :id
        AND r.deletedAt IS NULL
        AND r.isAdminRole = false
        AND r.isSystemRole = false
    """)
    Optional<Role> findValidById(@Param("id") @NonNull UUID id);

    @Query("""
        SELECT r
        FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.id = :id
        AND r.deletedAt IS NULL
        AND r.isSystemRole = false
    """)
    Optional<Role> findDetailById(@Param("id") @NonNull UUID id);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM users_roles ur
            WHERE ur.role_id = :roleId
        )
    """, nativeQuery = true)
    boolean existsUserAssignment(@Param("roleId") @NonNull UUID roleId);

}

