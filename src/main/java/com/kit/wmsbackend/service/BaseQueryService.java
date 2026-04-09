package com.kit.wmsbackend.service;

import com.kit.wmsbackend.dto.*;
import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.BaseSpecification;
import com.kit.wmsbackend.validator.SortValidator;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public abstract class BaseQueryService<T extends BaseAuditEntity> {
    protected Page<T> search(
            @NonNull QuerySpecification<T> querySpecification,
            @NonNull SortValidator sortValidator,
            @NonNull ListQueryFieldConfig<T> fieldConfig,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request,
            boolean includeDeleted
    ) {
        SearchRequest searchRequest = request.search();
        String keyword = searchRequest == null ? null : searchRequest.keyword();
        PaginationRequest pagination = request.pagination();

        SortRequest sort = request.sort();
        sortValidator.validate(sort, fieldConfig.sortableFields().keySet());

        int page = pagination.page() - 1;
        int size = pagination.size();

        Specification<T> baseSpec = includeDeleted
            ? BaseSpecification.deleted()
            : BaseSpecification.notDeleted();

        Specification<T> spec = Specification
                .where(baseSpec)
                .and(querySpecification.filterSpecification().filter(request.filters(), fieldConfig.filterableFields()))
                .and(querySpecification.searchSpecification().search(keyword, fieldConfig.searchableFields()))
                .and(querySpecification.sortSpecification().sort(sort, fieldConfig.sortableFields()));

        Pageable pageable = buildPageable(page, size);

        return repository.findAll(spec, pageable);
    }

    protected Page<T> search(
            @NonNull QuerySpecification<T> querySpecification,
            @NonNull SortValidator sortValidator,
            @NonNull ListQueryFieldConfig<T> fieldConfig,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request
    ) {
        return search(
                querySpecification,
                sortValidator,
                fieldConfig,
                repository,
                request,
                false
        );
    }

    protected Pageable buildPageable(
            int page,
            int size
    ) {
        return PageRequest.of(page, size);
    }
}