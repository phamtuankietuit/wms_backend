package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.dto.*;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
public final class ListResponseMapper {
    public static <T> @NonNull ListResponse<List<T>> toListResponse(
            @NonNull Page<T> page,
            @Nullable SortRequest sort,
            @Nullable List<FilterRequest> filters
    ) {
        return new ListResponse<>(
            page.getContent(),
            buildFilterResponse(filters),
            buildPaginationResponse(page),
            buildSortResponse(sort)
        );
    }

    private static List<FilterResponse> buildFilterResponse(
            @Nullable List<FilterRequest> filters
    ) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }

        return filters
                .stream()
                .map(filter -> new FilterResponse(
                        filter.field(),
                        filter.operator(),
                        filter.value()
                ))
                .toList();
    }

    /**
     * Builds a {@link PaginationResponse} from Spring Data {@link Page} metadata.
     *
     * @param <T> The type of elements in the page
     * @param page The Spring Data Page object. Must not be null.
     * @return A {@link PaginationResponse} with 1-based page number and pagination metadata
     */
    private static <T> PaginationResponse buildPaginationResponse(@NonNull Page<T> page) {
        return new PaginationResponse(
            page.getNumber() + 1,              // Convert 0-based (Spring) to 1-based (API)
            page.getSize(),                    // Page size (matches request)
            page.getTotalPages(),              // Number of pages available
            Math.toIntExact(page.getTotalElements()) // Total record count (safe cast for typical datasets)
        );
    }

    /**
     * Builds a {@link SortResponse} from a {@link SortRequest}.
     *
     * @param sort The original sort request. May be null.
     * @return A {@link SortResponse} with the same field and direction, or null if no sort was provided
     */
    private static SortResponse buildSortResponse(@Nullable SortRequest sort) {
        if (sort == null) {
            return null;
        }
        return new SortResponse(sort.field(), sort.direction());
    }
}