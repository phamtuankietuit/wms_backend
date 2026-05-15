package com.kit.wmsbackend.feature.user.listqueryfieldconfig;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import com.kit.wmsbackend.entity.BaseEntity_;
import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.UserWarehouse_;
import com.kit.wmsbackend.entity.User_;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import com.kit.wmsbackend.strategy.filter.GreaterThanOrEqualToFilterStrategy;
import com.kit.wmsbackend.strategy.filter.LessThanOrEqualToFilterStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class UserListQueryFieldConfig implements ListQueryFieldConfig<User> {
    private static final Map<String, SearchStrategy<User>> SEARCHABLE_FIELDS = Map.of(
            "name", new LikeIgnoreCaseStrategy<>(root -> root.get(User_.name)),
            "email", new LikeIgnoreCaseStrategy<>(root -> root.get(User_.email)),
            "code", new LikeIgnoreCaseStrategy<>(root -> root.get(User_.code))
    );

    private static final Map<String, Function<Root<User>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "name", root -> root.get(User_.name),
            "email", root -> root.get(User_.email),
            "code", root -> root.get(User_.code),
            "updatedAt", root -> root.get(BaseAuditEntity_.updatedAt),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt)
    );

    private static final Map<String, Map<String, FilterStrategy<User>>> FILTERABLE_FIELDS = Map.of(
            "role", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(User_.roles).get(BaseEntity_.id))
            ),
            "warehouse", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root
                            .join(User_.usersWarehouses)
                            .join(UserWarehouse_.warehouse)
                            .get(BaseEntity_.id))
            ),
            "createdAt", Map.of(
                    "gt or eq", new GreaterThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt)),
                    "lt or eq", new LessThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt))
            )
    );

    @Override
    public Map<String, SearchStrategy<User>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<User>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<User>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
