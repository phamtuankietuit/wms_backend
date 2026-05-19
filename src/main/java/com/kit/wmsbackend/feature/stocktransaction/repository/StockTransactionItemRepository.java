package com.kit.wmsbackend.feature.stocktransaction.repository;

import com.kit.wmsbackend.entity.StockTransactionItem;
import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface StockTransactionItemRepository extends BaseAuditRepository<StockTransactionItem> {
    @Override
    @EntityGraph(attributePaths = {"variant", "variant.product"})
    @NonNull
    Page<StockTransactionItem> findAll(@NonNull Specification<StockTransactionItem> spec, @NonNull Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END
            FROM StockTransactionItem i
            JOIN i.variant v
            JOIN v.product p
            JOIN i.stockTransaction st
            WHERE p.id IN :productIds
            AND i.deletedAt IS NULL
            AND st.deletedAt IS NULL
            AND st.status IN :statuses
            """)
    boolean existsOpenByProductIds(
            @Param("productIds") Collection<UUID> productIds,
            @Param("statuses") Collection<StockTransactionStatus> statuses
    );
}
