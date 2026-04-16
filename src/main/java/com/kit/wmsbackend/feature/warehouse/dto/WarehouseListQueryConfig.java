package com.kit.wmsbackend.feature.warehouse.dto;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.entity.Warehouse_;
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
public class WarehouseListQueryConfig implements ListQueryFieldConfig<Warehouse> {
    private static final Map<String, SearchStrategy<Warehouse>> SEARCHABLE_FIELDS = Map.of(
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(Warehouse_.code)),
            "name", new LikeIgnoreCaseStrategy<>(root -> root.get(Warehouse_.name))
    );

    private static final Map<String, Function<Root<Warehouse>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "code", root -> root.get(Warehouse_.code),
            "name", root -> root.get(Warehouse_.name),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt)
    );

    private static final Map<String, Map<String, FilterStrategy<Warehouse>>> FILTERABLE_FIELDS = Map.of(
            "isActive", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Warehouse_.isActive))
            )
    );

    @Override
    public Map<String, SearchStrategy<Warehouse>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<Warehouse>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<Warehouse>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
