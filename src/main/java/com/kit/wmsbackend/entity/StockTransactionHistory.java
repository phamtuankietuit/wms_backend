package com.kit.wmsbackend.entity;

import com.kit.wmsbackend.enums.StockTransactionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_transaction_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionHistory extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transaction_id", nullable = false)
    private StockTransaction stockTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransactionStatus toStatus;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
