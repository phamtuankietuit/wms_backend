package com.kit.wmsbackend.dto;

import java.util.List;

public record ListRequest (
        SearchRequest searchRequest,
        List<FilterRequest> filters,
        List<SortRequest> sorts,
        PaginationRequest pagination
) {
}
