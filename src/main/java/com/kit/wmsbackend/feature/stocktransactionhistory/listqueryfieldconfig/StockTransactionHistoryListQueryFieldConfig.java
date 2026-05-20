package com.kit.wmsbackend.feature.stocktransactionhistory.listqueryfieldconfig;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.entity.BaseEntity_;
import com.kit.wmsbackend.entity.StockTransactionHistory;
import com.kit.wmsbackend.entity.StockTransactionHistory_;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class StockTransactionHistoryListQueryFieldConfig implements ListQueryFieldConfig<StockTransactionHistory> {
    private static final Map<String, Function<Root<StockTransactionHistory>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "createdAt", root -> root.get("createdAt")
    );

    private static final Map<String, Map<String, FilterStrategy<StockTransactionHistory>>> FILTERABLE_FIELDS = Map.of(
            "creator", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(BaseAuditEntity_.creator).get(BaseEntity_.id))
            ),
            "stockTransaction", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(StockTransactionHistory_.stockTransaction).get(BaseEntity_.id))
            )
    );

    @Override
    public Map<String, SearchStrategy<StockTransactionHistory>> searchableFields() {
        return Map.of();
    }

    @Override
    public Map<String, Function<Root<StockTransactionHistory>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<StockTransactionHistory>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
