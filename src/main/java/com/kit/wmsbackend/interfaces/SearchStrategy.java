package com.kit.wmsbackend.interfaces;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface SearchStrategy<T> {
    Predicate apply(
            Root<T> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String normalizedKeyword
    );
}
