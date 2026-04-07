package com.kit.wmsbackend.interfaces;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import java.util.Map;
import java.util.function.Function;

/**
 * Generic contract for defining reusable list query fields per feature.
 * Each feature provides searchable and sortable fields for validation/query composition.
 */
public interface ListQueryFieldConfig<T extends BaseAuditEntity> {
    Map<String, SearchStrategy<T>> searchableFields();

    Map<String, Function<Root<T>, Path<?>>> sortableFields();

    Map<String, Map<String, FilterStrategy<T>>> filterableFields();
}
