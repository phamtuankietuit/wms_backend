package com.kit.wmsbackend.specification;


import com.kit.wmsbackend.dto.FilterRequest;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
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
                    case "gt" -> predicates.add(cb.greaterThan(path.as(Comparable.class), (Comparable) f.value()));
                    case "lt" -> predicates.add(cb.lessThan(path.as(Comparable.class), (Comparable) f.value()));
                    case "in" -> predicates.add(path.in((Collection<?>) f.value()));
                    default -> throw new IllegalStateException("Unexpected value: " + f.operator());
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

