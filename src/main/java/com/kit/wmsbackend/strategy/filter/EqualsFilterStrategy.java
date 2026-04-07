package com.kit.wmsbackend.strategy.filter;

import com.kit.wmsbackend.interfaces.FilterStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

@RequiredArgsConstructor
public class EqualsFilterStrategy<T> implements FilterStrategy<T> {
    private final Function<Root<T>, Path<?>> fieldResolver;

    @Override
    public Predicate apply(Root<T> root, @NonNull CriteriaBuilder cb, Path<?> path, Object value) {
        return cb.equal(fieldResolver.apply(root), value);
    }
}