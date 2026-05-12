package com.kit.wmsbackend.feature.user.repository;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseAuditRepository<User> {
    Optional<User> findByEmail(String email);

    @Query("""
            select distinct user from User user
            left join fetch user.roles role
            left join fetch role.permissions
            where user.email = :email
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
}

