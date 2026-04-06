package com.kit.wmsbackend.interfaces;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.List;
import java.util.Set;

/**
 * Generic contract for defining reusable list query fields per feature.
 * Each feature provides searchable and sortable fields for validation/query composition.
 */
public interface ListQueryFieldConfig<T extends BaseAuditEntity> {
    List<SingularAttribute<? super T, String>> searchableFields();

    Set<String> sortableFields();
}
