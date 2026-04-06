package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.dto.PaginationResponse;
import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.dto.SortResponse;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Utility class for converting Spring Data {@link Page} objects to structured {@link ListResponse} objects.
 * <p>
 * This mapper transforms paginated Spring Data results into a consistent API response format that includes
 * the actual data, pagination metadata, and sort information.
 * </p>
 *
 * <p>Usage across services: All services extending {@link com.kit.wmsbackend.service.BaseQueryService}
 * should use this mapper when transforming {@code Page<T>} results to API responses. This ensures
 * consistent pagination representation (1-based page numbers) and sort metadata propagation across all list endpoints.</p>
 */

@NoArgsConstructor
public final class ListResponseMapper {
    /**
     * Converts a Spring Data {@link Page} object to a structured {@link ListResponse}.
     *
     * @param <T> The type of elements in the page
     * @param page The Spring Data Page object containing paginated results. Must not be null.
     * @param sort The original sort request. May be null if sorting was not requested.
     * @return A {@link ListResponse} containing:
     *         <ul>
     *           <li>{@code data}: The list of items from the page</li>
     *           <li>{@code pagination}: Metadata about pagination (page number is converted to 1-based for API)</li>
     *           <li>{@code sort}: The sort information, or null if no sort was provided</li>
     *         </ul>
     */
    @Contract("_, _ -> new")
    public static <T> @NonNull ListResponse<List<T>> toListResponse(
            @NonNull Page<T> page,
            @Nullable SortRequest sort
    ) {
        return new ListResponse<>(
            page.getContent(),
            buildPaginationResponse(page),
            buildSortResponse(sort)
        );
    }

    /**
     * Builds a {@link PaginationResponse} from Spring Data {@link Page} metadata.
     *
     * @param <T> The type of elements in the page
     * @param page The Spring Data Page object. Must not be null.
     * @return A {@link PaginationResponse} with 1-based page number and pagination metadata
     */
    @Contract("_ -> new")
    private static <T> @NonNull PaginationResponse buildPaginationResponse(@NonNull Page<T> page) {
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