package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.utils.SortUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SortValidator {

    public void validate(
            SortRequest sort,
            @NonNull Collection<String> allowedFields
    ) {
        if (sort == null) {
            return;
        }

        if (sort.field() == null || sort.field().isBlank()) {
            throw new AppException(ErrorCode.SORT_FIELD_REQUIRED);
        }

        if (!allowedFields.contains(sort.field())) {
            throw new AppException(ErrorCode.SORT_INVALID_FIELD, sort.field() + ". Allowed fields: " + allowedFields);
        }

        if (sort.direction() == null || sort.direction().isBlank()) {
            throw new AppException(ErrorCode.SORT_DIRECTION_REQUIRED);
        }

        if (!SortUtils.isValidDirection(sort.direction())) {
            throw new AppException(ErrorCode.SORT_INVALID_DIRECTION, sort.direction());
        }
    }
}
