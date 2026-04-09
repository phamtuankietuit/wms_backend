package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductCreateResponse;
import jakarta.validation.Valid;

public interface ProductService {
    ProductCreateResponse create(@Valid ProductCreateRequest productRequest);
}
