package com.kit.wmsbackend.strategy.filter;

import com.kit.wmsbackend.interfaces.FilterStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import org.jspecify.annotations.NonNull;
import com.kit.wmsbackend.utils.CriteriaValueConverter;

import java.util.function.Function;

@RequiredArgsConstructor
public class EqualsFilterStrategy<T> implements FilterStrategy<T> {
    private final Function<Root<T>, Path<?>> fieldResolver;

    @Override
    public Predicate apply(Root<T> root, @NonNull CriteriaBuilder cb, Path<?> path, Object value) {
        if (value == null) {
            throw new AppException(ErrorCode.FILTER_INVALID_VALUE, "Value for 'eq' filter must not be null");
        }

        Path<?> resolvedPath = fieldResolver.apply(root);
        Object normalizedValue = CriteriaValueConverter.convert(value, resolvedPath.getJavaType());
        return cb.equal(resolvedPath, normalizedValue);
    }
}