package com.kit.wmsbackend.strategy.filter;

import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class LikeIgnoreCaseFilterStrategy<T> implements FilterStrategy<T> {
    private final Function<Root<T>, Path<?>> fieldResolver;

    @Override
    public Predicate apply(Root<T> root, CriteriaBuilder cb, Path<?> path, Object value) {
        if (value == null) {
            throw new AppException(ErrorCode.FILTER_INVALID_VALUE, "Value for 'like' filter must not be null");
        }

        String pattern = "%" + value.toString()
                .trim()
                .toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_")
                + "%";

        return cb.like(cb.lower(fieldResolver.apply(root).as(String.class)), pattern, '\\');
    }
}