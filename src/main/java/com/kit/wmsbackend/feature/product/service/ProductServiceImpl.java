package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.mapper.ProductMapper;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductCreateService productCreateService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest productRequest) {
        return productCreateService.create(productRequest);
    }

    @Override
    public ProductResponse getById(UUID id) {
        return productRepository.findDetailById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product",  "id", id.toString()));
    }

    @Override
    public Boolean isCodeExists(String code) {
        String normalized = StringNormalizeUtils.normalizeCode(code);
        return normalized != null && productRepository.existsByCode(normalized);
    }
}