package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductCreateResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ProductService {
    ProductResponse create(@Valid ProductCreateRequest productRequest);
    ProductResponse getById(@Valid UUID id);
    Boolean isCodeExists(@Valid String code);
}
