package com.kit.wmsbackend.feature.role.listqueryfieldconfig;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.entity.Role;
import com.kit.wmsbackend.entity.Role_;
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
public class RoleListQueryFieldConfig implements ListQueryFieldConfig<Role> {
    private static final Map<String, SearchStrategy<Role>> SEARCHABLE_FIELDS = Map.of(
            "name", new LikeIgnoreCaseStrategy<>(root -> root.get(Role_.name)),
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(Role_.code))
    );

    private static final Map<String, Function<Root<Role>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "name", root -> root.get(Role_.name),
            "code", root -> root.get(Role_.code),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt),
            "updatedAt", root -> root.get(BaseAuditEntity_.updatedAt)
    );

    private static final Map<String, Map<String, FilterStrategy<Role>>> FILTERABLE_FIELDS = Map.of(
            "isSystemRole", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Role_.isSystemRole))
            ),
            "isAdminRole", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Role_.isAdminRole))
            )
    );

    @Override
    public Map<String, SearchStrategy<Role>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<Role>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<Role>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
