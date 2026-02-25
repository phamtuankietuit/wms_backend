package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
