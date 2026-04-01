package com.kit.wmsbackend.service;

import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.SearchRequest;
import com.kit.wmsbackend.dto.SortRequest;
import com.kit.wmsbackend.entity.BaseAuditEntity;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import com.kit.wmsbackend.specification.FilterSpecification;
import com.kit.wmsbackend.specification.SearchSpecification;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public abstract class BaseQueryService<T extends BaseAuditEntity> {
    protected Page<T> search(
            @NonNull SearchSpecification<T> searchSpecification,
            @NonNull FilterSpecification<T> filterSpecification,
            @NonNull BaseAuditRepository<T> repository,
            @NonNull ListRequest request
    ) {

        SearchRequest searchRequest = request.searchRequest();
        List<FilterRequest> filterRequests = request.filters();
        List<SortRequest> sorts = request.sorts();
        int page = request.pagination().page() - 1;
        int size = request.pagination().size();

        Specification<T> spec = Specification
                .where(searchSpecification.search(searchRequest.keyword(), searchRequest.searchFields()))
                .and(filterSpecification.filter(filterRequests));

        Pageable pageable = buildPageable(page, size, sorts);

        return repository.findAll(spec, pageable);
    }

    protected Pageable buildPageable(
            int page,
            int size,
            List<SortRequest> sorts
    ) {
        if (sorts == null || sorts.isEmpty()) {
            return PageRequest.of(page, size);
        }

        List<Sort.Order> orders = sorts.stream()
                .map(s -> new Sort.Order(
                        Sort.Direction.fromString(s.direction()),
                        s.field()
                ))
                .toList();

        return PageRequest.of(page, size, Sort.by(orders));
    }
}