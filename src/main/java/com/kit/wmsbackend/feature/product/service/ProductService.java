package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.dto.list.ProductListResponse;
import com.kit.wmsbackend.feature.product.dto.update.ProductUpdateInfoRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductService {
    ListResponse<List<ProductListResponse>> list(@Valid ListRequest listRequest);
    ProductResponse create(@Valid ProductCreateRequest productRequest);
    ProductResponse getById(@Valid UUID id);
    Boolean isCodeExists(@Valid String code);
    ProductInfoResponse update(@Valid UUID id, @Valid ProductUpdateInfoRequest req);
    void softDeleteById(@Valid UUID id);
    void bulkSoftDelete(@Valid @NotEmpty Set<@NotNull UUID> ids);
    ProductResponse restoreById(@Valid UUID id);
    List<ProductResponse> bulkRestore(@Valid @NotEmpty Set<@NotNull UUID> ids);
}
