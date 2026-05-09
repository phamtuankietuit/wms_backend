package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.entity.BaseAuditEntity_;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class BaseSpecification {
    public static <T> Specification<T> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get(BaseAuditEntity_.DELETED_AT));
    }

    public static <T> Specification<T> deleted() {
        return (root, query, cb) ->
                cb.isNotNull(root.get(BaseAuditEntity_.DELETED_AT));
    }

    public static <T> Specification<T> isTrue(SingularAttribute<? super T, Boolean> field) {
        return (root, query, cb) ->
                cb.isTrue(root.get(field));
    }

    public static <T> Specification<T> isFalse(SingularAttribute<? super T, Boolean> field) {
        return (root, query, cb) ->
                cb.isFalse(root.get(field));
    }

    public static <T> Specification<T> fieldEquals(SingularAttribute<? super T, ?> field, Object value) {
        return (root, query, cb) ->
                cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> fieldIn(SingularAttribute<? super T, ?> field, Collection<?> values) {
        return (root, query, cb) ->
                root.get(field).in(values);
    }

    public static <T> Specification<T> fetchAudit() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("creator", JoinType.LEFT);
                root.fetch("updater", JoinType.LEFT);
                root.fetch("deleter", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
