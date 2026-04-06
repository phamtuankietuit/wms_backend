package com.kit.wmsbackend.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.PaginationRequest;
import com.kit.wmsbackend.dto.SearchRequest;
import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.AuditSpecification;
import com.kit.wmsbackend.specification.SearchSpecification;
import com.kit.wmsbackend.validator.SortValidator;
import com.kit.wmsbackend.utils.SortUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public abstract class BaseQueryService<T extends BaseAuditEntity> {
    /**
     * Search with explicit control over deleted record inclusion.
     *
     * @param searchSpecification The search specification with field mappings
     * @param sortValidator Validator for sort field and direction
     * @param fieldConfig Feature-specific field configuration
     * @param repository The repository for database access
     * @param request The list request (pagination, search, sort)
     * @param includeDeleted If true, includes logically deleted records; if false, excludes them
     * @return Paginated results matching the search criteria
     */
    protected Page<T> search(
            @NonNull SearchSpecification<T> searchSpecification,
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
        sortValidator.validate(sort, fieldConfig.sortableFields());

        int page = pagination.page() - 1;
        int size = pagination.size();

        // Include or exclude deleted records based on parameter
        Specification<T> auditSpecification = includeDeleted 
            ? AuditSpecification.deleted() 
            : AuditSpecification.notDeleted();

        Specification<T> spec = Specification
                .where(auditSpecification)
                .and(searchSpecification.search(keyword, fieldConfig.searchableFields()));

        Pageable pageable = buildPageable(page, size, sort);

        return repository.findAll(spec, pageable);
    }

    /**
     * Search with default behavior: excludes logically deleted records.
     * Convenience overload for the common case.
     *
     * @param searchSpecification The search specification with field mappings
     * @param sortValidator Validator for sort field and direction
     * @param fieldConfig Feature-specific field configuration
     * @param repository The repository for database access
     * @param request The list request (pagination, search, sort)
     * @return Paginated results matching the search criteria (deleted records excluded)
     */
    protected Page<T> search(
            @NonNull SearchSpecification<T> searchSpecification,
            @NonNull SortValidator sortValidator,
            @NonNull ListQueryFieldConfig<T> fieldConfig,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request
    ) {
        return search(searchSpecification, sortValidator, fieldConfig, repository, request, false);
    }

    protected Pageable buildPageable(
            int page,
            int size,
            SortRequest sort
    ) {
        if (sort == null) {
            return PageRequest.of(page, size);
        }

        Sort.Direction direction = SortUtils.toSpringDirection(sort.direction());
        Sort sortOrder = Sort.by(
                new Sort.Order(direction, sort.field())
        );

        return PageRequest.of(page, size, sortOrder);
    }
}