package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Warehouse extends BaseAuditEntity {
    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean isActive = true;

    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @OneToMany(mappedBy = "warehouse")
    private List<UserWarehouse> usersWarehouses;
}

