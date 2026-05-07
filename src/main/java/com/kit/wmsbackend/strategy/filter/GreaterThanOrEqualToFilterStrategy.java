package com.kit.wmsbackend.strategy.filter;

import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.utils.CriteriaValueConverter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class GreaterThanOrEqualToFilterStrategy<T> implements FilterStrategy<T> {
    private final Function<Root<T>, Path<?>> fieldResolver;

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Predicate apply(Root<T> root, CriteriaBuilder cb, Path<?> path, Object value) {
        Path resolvedPath = fieldResolver.apply(root);
        Object normalizedValue = CriteriaValueConverter.convert(value, resolvedPath.getJavaType());

        if (!(normalizedValue instanceof Comparable<?> comparableValue)) {
            throw new AppException(ErrorCode.FILTER_INVALID_VALUE, "Value for 'gt or eq' filter must implement Comparable");
        }

        return cb.greaterThanOrEqualTo(resolvedPath.as(comparableValue.getClass()), (Comparable) comparableValue);
    }
}