package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.entity.BaseAuditEntity_;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class AuditSpecification {
    @Contract(pure = true)
    public static <T extends BaseAuditEntity> @NonNull Specification<T> notDeleted() {
        return (root, query, cb) ->
                cb.isNull(root.get(BaseAuditEntity_.DELETED_AT));
    }

    @Contract(pure = true)
    public static <T extends BaseAuditEntity> @NonNull Specification<T> deleted() {
        return (root, query, cb) ->
                cb.isNotNull(root.get(BaseAuditEntity_.DELETED_AT));
    }

    @Contract(pure = true)
    public static <T> @NonNull Specification<T> active() {
        return (root, query, cb) ->
                cb.isTrue(root.get("isActive"));
    }

    @Contract(pure = true)
    public static <T> @NonNull Specification<T> inActive() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isActive"));
    }
}
