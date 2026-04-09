package com.kit.wmsbackend.assembler;

import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.dto.FilterResponse;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.dto.PaginationResponse;
import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.dto.SortResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListResponseAssembler {
    public <T> @NonNull ListResponse<List<T>> toListResponse(
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

    private List<FilterResponse> buildFilterResponse(
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

    private <T> PaginationResponse buildPaginationResponse(@NonNull Page<T> page) {
        return new PaginationResponse(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                Math.toIntExact(page.getTotalElements())
        );
    }

    private SortResponse buildSortResponse(@Nullable SortRequest sort) {
        if (sort == null) {
            return null;
        }

        return new SortResponse(sort.field(), sort.direction());
    }
}