package com.kit.wmsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_transaction_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StockTransactionItem extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transaction_id", nullable = false)
    private StockTransaction stockTransaction;

    @Column(nullable = false)
    private Long quantity;
}
