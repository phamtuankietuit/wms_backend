package com.kit.wmsbackend.feature.user.repository;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseAuditRepository<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permissions
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

    boolean existsByIdAndDeletedAtIsNull(UUID id);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.roles
            WHERE u.id IN :ids
            """)
    List<User> findAllWithRolesByIdIn(@Param("ids") List<UUID> ids);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.roles
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithRoles(@Param("id") @NonNull UUID id);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            WHERE u.id = :id
            AND u.deletedAt IS NULL
            AND NOT EXISTS (
                SELECT 1 FROM u.roles r
                WHERE r.isAdminRole = true OR r.isSystemRole = true
            )
            """)
    Optional<User> findForSoftDelete(@Param("id") @NonNull UUID id);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            WHERE u.id IN :ids
            AND u.deletedAt IS NULL
            AND NOT EXISTS (
                SELECT 1 FROM u.roles r
                WHERE r.isAdminRole = true OR r.isSystemRole = true
            )
            """)
    Collection<User> findAllForSoftDelete(@Param("ids") @NonNull Collection<UUID> ids);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            WHERE u.id IN :ids
            AND u.deletedAt IS NOT NULL
            AND NOT EXISTS (
                SELECT 1 FROM u.roles r
                WHERE r.isAdminRole = true OR r.isSystemRole = true
            )
            """)
    List<User> findAllDeletedForRestore(@Param("ids") @NonNull Collection<UUID> ids);
}

