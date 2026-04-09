package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductCreateService productCreateService;

    @Override
    @Transactional
    public ProductCreateResponse create(ProductCreateRequest productRequest) {
        return productCreateService.create(productRequest);
    }
}