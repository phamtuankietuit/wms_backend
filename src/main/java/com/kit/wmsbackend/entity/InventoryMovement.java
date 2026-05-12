package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_movements")
@NamedEntityGraph(
        name = "InventoryMovement.detail",
        attributeNodes = {
                @NamedAttributeNode("stockTransaction")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryMovement extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transaction_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private StockTransaction stockTransaction;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long quantityChange = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long beforeQuantity = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long afterQuantity = 0L;
}
