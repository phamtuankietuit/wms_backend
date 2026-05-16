package com.kit.wmsbackend.service;

import com.kit.wmsbackend.dto.*;
import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.*;
import com.kit.wmsbackend.validator.SortValidator;
import com.kit.wmsbackend.validator.FilterValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QueryService<T extends BaseAuditEntity> {
    FilterSpecification<T> filterSpecification;
    SearchSpecification<T> searchSpecification;
    SortSpecification<T> sortSpecification;
    SortValidator sortValidator;
    FilterValidator filterValidator;

    public Page<T> list(
            @NonNull ListQueryFieldConfig<T> listQueryFieldConfig,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request,
            boolean includeDeleted,
            boolean includeAudit
    ) {
        SearchRequest searchRequest = request.search();
        String keyword = searchRequest == null ? null : searchRequest.keyword();
        PaginationRequest pagination = request.pagination();

        SortRequest sort = request.sort();

        filterValidator.validate(request.filters(), listQueryFieldConfig.filterableFields());

        sortValidator.validate(sort, listQueryFieldConfig.sortableFields().keySet());

        Specification<T> baseSpec = includeDeleted
            ? BaseSpecification.deleted()
            : BaseSpecification.notDeleted();

        Specification<T> auditSpec = includeAudit
            ? BaseSpecification.fetchAudit()
            : null;

        Specification<T> spec = Specification.where(baseSpec);

        Specification<T> filterSpec = filterSpecification.filter(request.filters(), listQueryFieldConfig.filterableFields());
        spec = spec.and(filterSpec);


        Specification<T> searchSpec = searchSpecification.search(keyword, listQueryFieldConfig.searchableFields());
        if (searchSpec != null) {
            spec = spec.and(searchSpec);
        }

        if (auditSpec != null) {
            spec = spec.and(auditSpec);
        }

        Specification<T> sortSpec = sortSpecification.sort(sort, listQueryFieldConfig.sortableFields());
        if (sortSpec != null) {
            spec = spec.and(sortSpec);
        }

        Pageable pageable = PageRequest.of(pagination.page() - 1, pagination.size());

        return repository.findAll(spec, pageable);
    }

    public Page<T> list(
            @NonNull ListQueryFieldConfig<T> listQueryFieldConfig,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request
    ) {
        return list(
                listQueryFieldConfig,
                repository,
                request,
                false,
                false
        );
    }
}