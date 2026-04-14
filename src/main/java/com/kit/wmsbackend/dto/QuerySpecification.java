package com.kit.wmsbackend.dto;

import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.specification.FilterSpecification;
import com.kit.wmsbackend.specification.SearchSpecification;
import com.kit.wmsbackend.specification.SortSpecification;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public record QuerySpecification<T extends BaseAuditEntity>(
        @NotNull
        SearchSpecification<T> searchSpecification,

        @NotNull
        FilterSpecification<T> filterSpecification,

        @NotNull
        SortSpecification<T> sortSpecification
) {
}
