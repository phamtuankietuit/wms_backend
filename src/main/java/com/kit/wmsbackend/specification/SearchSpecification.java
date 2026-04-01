package com.kit.wmsbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SearchSpecification<T> {
    public Specification<T> search(String keyword, List<String> fields) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            List<Predicate> predicates = fields
                    .stream()
                    .map(field -> cb.like(cb.lower(root.get(field)), pattern))
                    .toList();

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
