package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionGroup extends BaseAuditEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<Permission> permissions = new ArrayList<>();
}

