package com.kit.wmsbackend.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;

@Component
public class SearchSpecification<T> {
    public Specification<T> search(
            String keyword,
            List<SingularAttribute<? super T, String>> fields
    ) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String normalized = Normalizer
                    .normalize(keyword, Normalizer.Form.NFKC)
                    .trim()
                    .toLowerCase();

            String pattern = escapeLikePattern(normalized);

            List<Predicate> predicates = fields
                    .stream()
                    .map(field -> cb.like(cb.lower(root.get(field)), pattern, '\\'))
                    .toList();

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private @NonNull String escapeLikePattern(@NonNull String keyword) {
        return "%" + keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_")
                + "%";
    }
}
