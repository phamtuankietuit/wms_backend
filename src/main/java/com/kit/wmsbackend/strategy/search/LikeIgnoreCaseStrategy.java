package com.kit.wmsbackend.strategy.search;

import com.kit.wmsbackend.interfaces.SearchStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class LikeIgnoreCaseStrategy<T> implements SearchStrategy<T> {
    private final Function<Root<T>, Path<String>> fieldResolver;

    @Override
    public Predicate apply(
            Root<T> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String normalizedKeyword
    ) {
        if (normalizedKeyword == null || normalizedKeyword.isBlank()) {
            return null;
        }

        String pattern = "%" + normalizedKeyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_")
                + "%";

        Path<String> fieldPath = fieldResolver.apply(root);
        return cb.like(cb.lower(fieldPath), pattern, '\\');
    }
}
