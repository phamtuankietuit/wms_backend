package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class SortSpecification<T extends BaseAuditEntity> {
    public Specification<T> sort(
            SortRequest sort,
            Map<String, Function<Root<T>, Path<?>>> sortableFields
    ) {
        return (root, query, cb) -> {
            if (sort == null || sortableFields.isEmpty() || isCountQuery(query)) {
                return null;
            }

            Function<Root<T>, Path<?>> pathResolver = sortableFields.get(sort.field());
            if (pathResolver == null) {
                return null;
            }

            Path<?> sortPath = pathResolver.apply(root);
            if (sortPath == null) {
                return null;
            }

            String direction = SortUtils.normalizeDirection(sort.direction());
            if ("desc".equals(direction)) {
                query.orderBy(cb.desc(sortPath));
            } else {
                query.orderBy(cb.asc(sortPath));
            }

            return null;
        };
    }

    private boolean isCountQuery(@NonNull CriteriaQuery<?> query) {
        Class<?> resultType = query.getResultType();
        return Long.class.equals(resultType) || long.class.equals(resultType);
    }
}
