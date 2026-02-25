package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseAuditEntity {
    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @OneToMany(mappedBy = "warehouse")
    private List<UserWarehouse> usersWarehouses;
}
