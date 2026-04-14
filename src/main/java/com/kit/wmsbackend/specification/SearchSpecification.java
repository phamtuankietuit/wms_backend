package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.interfaces.SearchStrategy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class SearchSpecification<T> {
    public Specification<T> search(
            String keyword,
            Map<String, SearchStrategy<T>> fields
    ) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank() || fields.isEmpty()) {
                return null;
            }

            String normalized = Normalizer
                    .normalize(keyword, Normalizer.Form.NFKC)
                    .trim()
                    .toLowerCase();

            List<Predicate> predicates = fields
                    .values()
                    .stream()
                    .map(strategy -> strategy.apply(root, query, cb, normalized))
                    .filter(Objects::nonNull)
                    .toList();

            if (predicates.isEmpty()) {
                return null;
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
