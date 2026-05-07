package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilterValidator {

    public <E> void validate(
            List<FilterRequest> filters,
            @NonNull Map<String, Map<String, FilterStrategy<E>>> filterableFields
    ) {
        if (filters == null || filters.isEmpty()) {
            return;
        }

        Set<String> seen = new HashSet<>();

        for (FilterRequest filter : filters) {
            validateSingle(filter, filterableFields, seen);
        }
    }

    private <T> void validateSingle(
            FilterRequest filter,
            @NonNull Map<String, Map<String, FilterStrategy<T>>> filterableFields,
            Set<String> seen
    ) {
        if (filter == null || filter.field() == null || filter.field().isBlank()) {
            throw new AppException(ErrorCode.FILTER_INVALID_FIELD);
        }

        String field = filter.field();

        String operator = filter.operator() == null ? "" : filter.operator();
        String value = String.valueOf(filter.value());
        String composite = field + "::" + operator + "::" + value;

        if (seen.contains(composite)) {
            throw new AppException(ErrorCode.FILTER_DUPLICATE_FIELD, field + " : " + value);
        }
        seen.add(composite);

        if (filter.operator() == null || filter.operator().isBlank()) {
            throw new AppException(ErrorCode.FILTER_INVALID_OPERATOR, field);
        }

        Map<String, ?> operators = filterableFields.get(field);
        if (operators == null || operators.isEmpty()) {
            throw new AppException(ErrorCode.FILTER_INVALID_FIELD, field);
        }

        if (!operators.containsKey(filter.operator())) {
            throw new AppException(ErrorCode.FILTER_INVALID_OPERATOR, filter.operator() + " : " + field);
        }
    }
}
