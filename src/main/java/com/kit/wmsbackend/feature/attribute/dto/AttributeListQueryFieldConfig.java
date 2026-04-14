package com.kit.wmsbackend.feature.attribute.dto;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.Attribute_;
import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import com.kit.wmsbackend.strategy.filter.GreaterThanFilterStrategy;
import com.kit.wmsbackend.strategy.filter.LessThanFilterStrategy;
import com.kit.wmsbackend.strategy.filter.LikeIgnoreCaseFilterStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class AttributeListQueryFieldConfig implements ListQueryFieldConfig<Attribute> {
    private static final Map<String, SearchStrategy<Attribute>> SEARCHABLE_FIELDS = Map.of(
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(Attribute_.code)),
            "name", new LikeIgnoreCaseStrategy<>(root -> root.get(Attribute_.name))
    );

    private static final Map<String, Function<Root<Attribute>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "code", root -> root.get(Attribute_.code),
            "name", root -> root.get(Attribute_.name),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt),
            "isActive", root -> root.get(Attribute_.isActive)
    );

    private static final Map<String, Map<String, FilterStrategy<Attribute>>> FILTERABLE_FIELDS = Map.of(
            "code", Map.of(
                "eq", new EqualsFilterStrategy<>(root -> root.get(Attribute_.code)),
                "like", new LikeIgnoreCaseFilterStrategy<>(root -> root.get(Attribute_.code))
            ),
            "name", Map.of(
                "like", new LikeIgnoreCaseFilterStrategy<>(root -> root.get(Attribute_.name))
            ),
            "isActive", Map.of(
                "eq", new EqualsFilterStrategy<>(root -> root.get(Attribute_.isActive))
            ),
            "createdAt", Map.of(
                "gt", new GreaterThanFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt)),
                "lt", new LessThanFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt))
            )
        );

    @Override
    public Map<String, SearchStrategy<Attribute>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<Attribute>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<Attribute>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
