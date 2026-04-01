package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.feature.product.dto.ProductRequest;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProductService {
    ProductResponse create(@Valid @RequestBody ProductRequest productRequest);
}
