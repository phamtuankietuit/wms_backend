package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.*;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class StockTransactionListQueryFieldConfig implements ListQueryFieldConfig<StockTransaction> {
    private static final Map<String, SearchStrategy<StockTransaction>> SEARCHABLE_FIELDS = Map.of(
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(StockTransaction_.code)),
            "warehouseName", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransaction_.warehouse).get(Warehouse_.name)),
            "warehouseCode", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransaction_.warehouse).get(Warehouse_.code)),
            "assignedToName", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransaction_.assignedTo).get(User_.name)),
            "assignedToEmail", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransaction_.assignedTo).get(User_.email))
    );

    private static final Map<String, Function<Root<StockTransaction>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt),
            "updatedAt", root -> root.get(BaseAuditEntity_.updatedAt),
            "status", root -> root.get(StockTransaction_.status),
            "type", root -> root.get(StockTransaction_.type)
    );

    private static final Map<String, Map<String, FilterStrategy<StockTransaction>>> FILTERABLE_FIELDS = Map.of(
            "status", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(StockTransaction_.status))
            ),
            "type", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(StockTransaction_.type))
            ),
            "warehouse", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(StockTransaction_.warehouse))
            ),
            "assignedTo", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(StockTransaction_.assignedTo).get(BaseEntity_.id))
            ),
            "createdAt", Map.of(
                    "gt or eq", new GreaterThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt)),
                    "lt or eq", new LessThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt))
            )
    );


    @Override
    public Map<String, SearchStrategy<StockTransaction>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<StockTransaction>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<StockTransaction>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
