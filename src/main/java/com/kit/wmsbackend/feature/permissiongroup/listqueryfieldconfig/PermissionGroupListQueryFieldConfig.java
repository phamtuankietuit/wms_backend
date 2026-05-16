package com.kit.wmsbackend.feature.permissiongroup.listqueryfieldconfig;

import com.kit.wmsbackend.entity.PermissionGroup;
import com.kit.wmsbackend.entity.PermissionGroup_;
import com.kit.wmsbackend.entity.Permission_;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class PermissionGroupListQueryFieldConfig implements ListQueryFieldConfig<PermissionGroup> {
    private static final Map<String, SearchStrategy<PermissionGroup>> SEARCHABLE_FIELDS = Map.of(
        "code", new LikeIgnoreCaseStrategy<>(root -> root.get(PermissionGroup_.code)),
        "name", new LikeIgnoreCaseStrategy<>(root -> root.get(PermissionGroup_.name)),
        "permissionCode", new LikeIgnoreCaseStrategy<>(root -> root.join(PermissionGroup_.permissions).get(Permission_.code)),
        "permissionName", new LikeIgnoreCaseStrategy<>(root -> root.join(PermissionGroup_.permissions).get(Permission_.name))
    );

    private static final Map<String, Function<Root<PermissionGroup>, Path<?>>> SORTABLE_FIELDS = Map.of(
        "code", root -> root.get(PermissionGroup_.code),
        "name", root -> root.get(PermissionGroup_.name)
    );

    @Override
    public Map<String, SearchStrategy<PermissionGroup>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<PermissionGroup>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<PermissionGroup>>> filterableFields() {
        return Map.of();
    }
}
