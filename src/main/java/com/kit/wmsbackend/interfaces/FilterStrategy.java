package com.kit.wmsbackend.interfaces;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface FilterStrategy<T> {
    Predicate apply(Root<T> root, CriteriaBuilder cb, Path<?> path, Object value);
}