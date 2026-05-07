package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FilterSpecification<T> {
    public @NonNull Specification<T> filter(
            List<FilterRequest> filters,
            Map<String, Map<String, FilterStrategy<T>>> filterableFields
    ) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            for (FilterRequest filter : filters) {
                FilterStrategy<T> strategy = resolveStrategy(filter, filterableFields);
                Path<?> path = root.get(filter.field());
                predicates.add(applyStrategy(strategy, root, cb, path, filter));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private @NonNull FilterStrategy<T> resolveStrategy(
            @NonNull FilterRequest filter,
            @NonNull Map<String, Map<String, FilterStrategy<T>>> filterableFields
    ) {
        Map<String, FilterStrategy<T>> operators = filterableFields.get(filter.field());
        if (operators == null || operators.isEmpty()) {
            throw new AppException(ErrorCode.FILTER_INVALID_FIELD, filter.field());
        }

        FilterStrategy<T> strategy = operators.get(filter.operator());
        if (strategy == null) {
            throw new AppException(ErrorCode.FILTER_INVALID_OPERATOR, filter.operator() + " : " + filter.field());
        }

        return strategy;
    }

    private Predicate applyStrategy(
            FilterStrategy<T> strategy,
            Root<T> root,
            CriteriaBuilder cb,
            Path<?> path,
            FilterRequest filter
    ) {
        try {
            return strategy.apply(root, cb, path, filter.value());
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILTER_INVALID_VALUE_FOR_FIELD, filter.field() + " : "+  filter.operator());
        }
    }
}

