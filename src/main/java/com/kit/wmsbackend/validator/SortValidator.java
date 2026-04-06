package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.exception.BadRequestException;
import com.kit.wmsbackend.utils.SortUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates a single sort request against a set of allowed field names.
 * Prevents injection of unauthorized fields and invalid sort directions.
 */
@Component
public class SortValidator {
    /**
     * Validates a sort request against allowed fields.
     * Throws BadRequestException if sort field is not allowed or direction is invalid.
     *
     * @param sort the sort request to validate (null is safe, skips validation)
     * @param allowedFields allowed field names for this entity
     * @throws BadRequestException if validation fails
     */
    public void validate(
            SortRequest sort,
            @NonNull Set<String> allowedFields
    ) {
        if (sort == null) {
            return;
        }

        if (sort.field() == null || sort.field().isBlank()) {
            throw new BadRequestException("Sort field cannot be empty");
        }

        if (!allowedFields.contains(sort.field())) {
            throw new BadRequestException(
                    "Invalid sort field: " + sort.field() +
                ". Allowed fields: " + allowedFields
            );
        }

        if (sort.direction() == null || sort.direction().isBlank()) {
            throw new BadRequestException("Sort direction cannot be empty");
        }

        if (!SortUtils.isValidDirection(sort.direction())) {
            throw new BadRequestException(
                    "Invalid sort direction: " + sort.direction() +
                    ". Must be 'asc' or 'desc'"
            );
        }
    }
}
