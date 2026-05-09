package com.kit.wmsbackend.feature.stocktransaction.repository;

import com.kit.wmsbackend.entity.StockTransactionItem;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

public interface StockTransactionItemRepository extends BaseAuditRepository<StockTransactionItem> {
    @Override
    @EntityGraph(attributePaths = {"variant", "variant.product"})
    @NonNull
    Page<StockTransactionItem> findAll(@NonNull Specification<StockTransactionItem> spec, @NonNull Pageable pageable);
}
