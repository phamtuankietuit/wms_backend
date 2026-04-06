package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.dto.FilterRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterSpecification<T> {
    public @NonNull Specification<T> filter(List<FilterRequest> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            for (FilterRequest f : filters) {
                Path<Object> path = root.get(f.field());

                switch (f.operator()) {
                    case "eq" -> predicates.add(cb.equal(path, f.value()));
                    case "like" -> predicates.add(
                            cb.like(cb.lower(path.as(String.class)),
                                    "%" + f.value().toString().toLowerCase() + "%")
                    );
                    case "gt" -> predicates.add(buildGreaterThanPredicate(cb, path, f.value()));
                    case "lt" -> predicates.add(buildLessThanPredicate(cb, path, f.value()));
                    case "in" -> predicates.add(path.in((Collection<?>) f.value()));
                    default -> throw new IllegalStateException("Unexpected value: " + f.operator());
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate buildGreaterThanPredicate(CriteriaBuilder cb, Path<Object> path, Object value) {
        ComparableValue comparable = toComparableValue(path, value);
        return comparable.greaterThan(cb);
    }

    private Predicate buildLessThanPredicate(CriteriaBuilder cb, Path<Object> path, Object value) {
        ComparableValue comparable = toComparableValue(path, value);
        return comparable.lessThan(cb);
    }

    @Contract("_, null -> fail")
    private @NonNull ComparableValue toComparableValue(Path<Object> path, Object value) {
        if (!(value instanceof Comparable<?> comparableValue)) {
            throw new IllegalArgumentException("Value for comparison must implement Comparable");
        }

        return new ComparableValue(path, comparableValue);
    }

    private record ComparableValue(
            Path<Object> path,
            Comparable<?> value
    ) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        private Predicate greaterThan(@NonNull CriteriaBuilder cb) {
            return cb.greaterThan(path.as((Class) value.getClass()), (Comparable) value);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private Predicate lessThan(@NonNull CriteriaBuilder cb) {
            return cb.lessThan(path.as((Class) value.getClass()), (Comparable) value);
        }
    }
}

