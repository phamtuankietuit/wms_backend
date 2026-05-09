package com.kit.wmsbackend.feature.stocktransaction.listqueryfieldconfig;

import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class StockTransactionItemListQueryFieldConfig implements ListQueryFieldConfig<StockTransactionItem> {
    private static final Map<String, SearchStrategy<StockTransactionItem>> SEARCHABLE_FIELDS = Map.of(
            "variantSku", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransactionItem_.variant).get(Variant_.sku)),
            "productCode", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransactionItem_.variant).join(Variant_.product).get(Product_.code)),
            "productName", new LikeIgnoreCaseStrategy<>(root -> root.join(StockTransactionItem_.variant).join(Variant_.product).get(Product_.name))
    );

    private static final Map<String, Map<String, FilterStrategy<StockTransactionItem>>> FILTERABLE_FIELDS = Map.of(
            "stockTransactionId", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(StockTransactionItem_.stockTransaction).get(BaseEntity_.id))
            )
    );

    @Override
    public Map<String, SearchStrategy<StockTransactionItem>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<StockTransactionItem>, Path<?>>> sortableFields() {
        return Map.of();
    }

    @Override
    public Map<String, Map<String, FilterStrategy<StockTransactionItem>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
