package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.dto.list.ProductListQueryFieldConfig;
import com.kit.wmsbackend.feature.product.dto.list.ProductListResponse;
import com.kit.wmsbackend.feature.product.dto.update.ProductUpdateInfoRequest;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.mapper.ProductMapper;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.utils.SecurityUtils;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductCreateService productCreateService;
    ProductRepository productRepository;
    ProductMapper productMapper;
    ListResponseAssembler listResponseAssembler;
    QueryService<Product> queryService;
    ProductListQueryFieldConfig listQueryFieldConfig;

    @Override
    public ListResponse<List<ProductListResponse>> list(ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
              queryService.list(listQueryFieldConfig, productRepository, listRequest)
                        .map(productMapper::toProductListResponse),
                listRequest.sort(),
                listRequest.filters()
        );
    }

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest productRequest) {
        return productCreateService.create(productRequest);
    }

    @Override
    public ProductResponse getById(UUID id) {
        return productRepository.findDetailById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, id.toString()));
    }

    @Override
    public Boolean isCodeExists(String code) {
        String normalized = StringNormalizeUtils.normalizeCode(code);
        return normalized != null && productRepository.existsByCode(normalized);
    }

    @Override
    @Transactional
    public ProductInfoResponse update(UUID id, ProductUpdateInfoRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id: " + id));

        productMapper.updateProduct(product, req);

        return productMapper.toProductInfoResponse(productRepository.save(product));

    }

    @Override
    @Transactional
    public void softDeleteById(UUID id) {
        Product product = productRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, id.toString()));

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("delete product");
        productRepository.softDelete(product, currentUserId);
    }

    @Override
    @Transactional
    public ProductResponse restoreById(UUID id) {
        Product product = productRepository.findDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, id.toString()));

        return productMapper.toProductResponse(productRepository.restore(product));
    }
}
