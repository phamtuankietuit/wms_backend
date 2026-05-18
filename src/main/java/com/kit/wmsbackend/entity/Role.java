package com.kit.wmsbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_roles_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseAuditEntity{
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "is_admin_role", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isAdminRole = false;

    @Column(name = "is_system_role", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isSystemRole = false;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}

