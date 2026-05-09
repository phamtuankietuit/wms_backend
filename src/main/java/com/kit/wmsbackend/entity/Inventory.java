package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"variant_id", "warehouse_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseAuditEntity {
    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long quantity = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long reservedQuantity = 0L;

    public Long getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}
