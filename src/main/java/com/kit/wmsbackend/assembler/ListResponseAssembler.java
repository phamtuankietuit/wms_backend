package com.kit.wmsbackend.assembler;

import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.dto.PaginationResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListResponseAssembler {
    public <T> @NonNull ListResponse<List<T>> toListResponse(
            @NonNull Page<T> page
    ) {
        return new ListResponse<>(
                page.getContent(),
                buildPaginationResponse(page)
        );
    }

    private <T> PaginationResponse buildPaginationResponse(@NonNull Page<T> page) {
        return new PaginationResponse(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages(),
                Math.toIntExact(page.getTotalElements())
        );
    }
}