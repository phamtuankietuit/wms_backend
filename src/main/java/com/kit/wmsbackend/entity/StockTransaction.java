package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.enums.StockTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StockTransaction extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "stockTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockTransactionItem> stockTransactionItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionStatus status = StockTransactionStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String note;
}
