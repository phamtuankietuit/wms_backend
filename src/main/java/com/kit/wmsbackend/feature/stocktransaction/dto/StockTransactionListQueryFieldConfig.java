package com.kit.wmsbackend.feature.stocktransaction.dto;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.util.Map;
import java.util.function.Function;

public class StockTransactionListQueryFieldConfig implements ListQueryFieldConfig<StockTransaction> {
    @Override
    public Map<String, SearchStrategy<StockTransaction>> searchableFields() {
        return Map.of();
    }

    @Override
    public Map<String, Function<Root<StockTransaction>, Path<?>>> sortableFields() {
        return Map.of();
    }

    @Override
    public Map<String, Map<String, FilterStrategy<StockTransaction>>> filterableFields() {
        return Map.of();
    }
}
