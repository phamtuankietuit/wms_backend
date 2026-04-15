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

    @OneToMany(mappedBy = "warehouse")
    private List<UserWarehouse> usersWarehouses;
}

