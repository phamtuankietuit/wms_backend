package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.enums.StockTransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_transactions", indexes = {
        @Index(name = "idx_stock_transaction_created_by", columnList = "created_by"),
        @Index(name = "idx_stock_transaction_updated_by", columnList = "updated_by"),
        @Index(name = "idx_stock_transaction_deleted_by", columnList = "deleted_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StockTransaction extends BaseAuditEntity {
    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @Fetch(FetchMode.SELECT)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    @Fetch(FetchMode.SELECT)
    private User assignedTo;

    @OneToMany(mappedBy = "stockTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockTransactionItem> stockTransactionItems = new ArrayList<>();

    @OneToMany(mappedBy = "stockTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockTransactionHistory> stockTransactionHistories = new ArrayList<>();

    @OneToMany(mappedBy = "stockTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionStatus status = StockTransactionStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String note;

    public void addStockTransactionItems(@NonNull List<StockTransactionItem> items) {
        for (StockTransactionItem item : items) {
            addStockTransactionItem(item);
        }
    }

    public void removeStockTransactionItems(@NonNull List<StockTransactionItem> items) {
        for (StockTransactionItem item : items) {
            removeStockTransactionItem(item);
        }
    }

    public void addStockTransactionItem(StockTransactionItem item) {
        stockTransactionItems.add(item);
        item.setStockTransaction(this);
    }

    public void removeStockTransactionItem(StockTransactionItem item) {
        stockTransactionItems.remove(item);
        item.setStockTransaction(null);
    }
}
