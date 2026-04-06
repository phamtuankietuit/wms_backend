package com.kit.wmsbackend.feature.attribute.dto;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.Attribute_;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import jakarta.persistence.metamodel.SingularAttribute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Reusable list query fields for Attribute feature.
 * This class is designed to be mirrored by other features for consistent list-query behavior.
 */
@Component
public class AttributeListQueryFieldConfig implements ListQueryFieldConfig<Attribute> {
    private static final Set<String> SORTABLE_FIELDS = Set.of("code", "name", "createdAt", "isActive");

    @Override
    public List<SingularAttribute<? super Attribute, String>> searchableFields() {
        return List.of(Attribute_.code, Attribute_.name);
    }

    @Override
    public Set<String> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Contract(value = " -> new", pure = true)
    public static @NonNull @Unmodifiable List<SingularAttribute<? super Attribute, String>> getSearchableFields() {
        return List.of(Attribute_.code, Attribute_.name);
    }

    public static Set<String> getSortableFields() {
        return SORTABLE_FIELDS;
    }
}
