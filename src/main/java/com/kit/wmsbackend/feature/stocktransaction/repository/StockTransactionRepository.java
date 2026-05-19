package com.kit.wmsbackend.feature.stocktransaction.repository;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockTransactionRepository extends BaseAuditRepository<StockTransaction> {
    @Query("""
        SELECT DISTINCT st
        FROM StockTransaction st
        LEFT JOIN FETCH st.stockTransactionItems
        WHERE st.id = :id
        AND st.deletedAt IS NULL
        """)
    Optional<StockTransaction> findWithItemsById(@Param("id") UUID id);


    @Query("""
        SELECT DISTINCT st
        FROM StockTransaction st
        LEFT JOIN FETCH st.stockTransactionItems
        WHERE st.id IN :ids
        AND st.deletedAt IS NULL
        """)
    List<StockTransaction> findAllWithItemsByIdIn(@Param("ids") Collection<UUID> ids);
}
