package com.kit.wmsbackend.feature.product.dto.list;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.Product_;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class ProductListQueryFieldConfig implements ListQueryFieldConfig<Product> {
    private static final Map<String, SearchStrategy<Product>> SEARCHABLE_FIELDS = Map.of(
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(Product_.code)),
            "name", new LikeIgnoreCaseStrategy<>(root -> root.get(Product_.name))
    );

    private static final Map<String, Map<String, FilterStrategy<Product>>> FILTERABLE_FIELDS = Map.of(
            "isActive", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Product_.isActive))
            )
    );

    private static final Map<String, Function<Root<Product>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "code", root -> root.get(Product_.code),
            "name", root -> root.get(Product_.name),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt),
            "isActive", root -> root.get(Product_.isActive)
    );

    @Override
    public Map<String, SearchStrategy<Product>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<Product>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<Product>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
