package com.kit.wmsbackend.specification;

import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
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
                FilterStrategy<T> strategy = filterableFields.get(filter.field()).get(filter.operator());
                predicates.add(applyStrategy(strategy, root, cb, filter));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate applyStrategy(
            FilterStrategy<T> strategy,
            Root<T> root,
            CriteriaBuilder cb,
            FilterRequest filter
    ) {
        try {
            return strategy.apply(root, cb, filter.value());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILTER_INVALID_VALUE_FOR_FIELD, filter.value() + " : " + filter.field());
        }
    }
}

