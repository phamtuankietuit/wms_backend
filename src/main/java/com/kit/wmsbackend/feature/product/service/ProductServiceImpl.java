package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.enums.StockTransactionStatus;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.dto.list.ProductListQueryFieldConfig;
import com.kit.wmsbackend.feature.product.dto.list.ProductListResponse;
import com.kit.wmsbackend.feature.product.dto.update.ProductUpdateInfoRequest;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.feature.stocktransaction.repository.StockTransactionItemRepository;
import com.kit.wmsbackend.feature.variant.repository.VariantRepository;
import com.kit.wmsbackend.mapper.ProductMapper;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.utils.SecurityUtils;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import com.kit.wmsbackend.utils.ValidateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    private static final Set<StockTransactionStatus> OPEN_STOCK_TRANSACTION_STATUSES = EnumSet.of(
            StockTransactionStatus.DRAFT,
            StockTransactionStatus.PENDING,
            StockTransactionStatus.CONFIRMED,
            StockTransactionStatus.PROCESSING
    );

    ProductCreateService productCreateService;
    ProductRepository productRepository;
    VariantRepository variantRepository;
    InventoryRepository inventoryRepository;
    StockTransactionItemRepository stockTransactionItemRepository;
    ProductMapper productMapper;
    ListResponseAssembler listResponseAssembler;
    QueryService<Product> queryService;
    ProductListQueryFieldConfig listQueryFieldConfig;
    ValidateUtils validateUtils;

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
        softDelete(Set.of(id), "delete product");
    }

    @Override
    @Transactional
    public void bulkSoftDelete(Set<UUID> ids) {
        softDelete(ids, "bulk delete products");
    }

    @Override
    @Transactional
    public ProductResponse restoreById(UUID id) {
        List<Product> products = productRepository.findAllDeletedWithVariantsByIdIn(Set.of(id));

        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, id.toString());
        }

        restoreProductAndVariants(products);

        return productMapper.toProductResponse(products.getFirst());
    }

    @Override
    @Transactional
    public List<ProductResponse> bulkRestore(Set<UUID> ids) {
        List<Product> products = productRepository.findAllDeletedWithVariantsByIdIn(ids);

        validateUtils.validateEntitiesExist(products, ids, ErrorCode.PRODUCT_NOT_FOUND);

        restoreProductAndVariants(products);

        return products
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    private void softDelete(Set<UUID> ids, String action) {
        List<Product> products = productRepository.findAllNotDeletedWithVariantsByIdIn(ids);

        validateUtils.validateEntitiesExist(products, ids, ErrorCode.PRODUCT_NOT_FOUND);
        validateCanSoftDelete(ids);

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem(action);

        List<Variant> variants = products
                .stream()
                .flatMap(product -> product.getVariants().stream())
                .filter(variant -> !variant.isDeleted())
                .toList();

        variantRepository.softDeleteAll(variants, currentUserId);
        productRepository.softDeleteAll(products, currentUserId);
    }

    private void validateCanSoftDelete(Set<UUID> ids) {
        if (inventoryRepository.existsQuantityByProductIds(ids)) {
            throw new AppException(ErrorCode.PRODUCT_CANNOT_DELETE_HAS_INVENTORY);
        }

        if (inventoryRepository.existsReservedQuantityByProductIds(ids)) {
            throw new AppException(ErrorCode.PRODUCT_CANNOT_DELETE_HAS_RESERVED_INVENTORY);
        }

        if (stockTransactionItemRepository.existsOpenByProductIds(ids, OPEN_STOCK_TRANSACTION_STATUSES)) {
            throw new AppException(ErrorCode.PRODUCT_CANNOT_DELETE_HAS_OPEN_STOCK_TRANSACTION);
        }
    }

    private void restoreProductAndVariants(@NonNull List<Product> products) {
        List<Variant> variants = products
                .stream()
                .flatMap(product -> product.getVariants().stream())
                .filter(Variant::isDeleted)
                .toList();

        variantRepository.restoreAll(variants);
        productRepository.restoreAll(products);
    }
}
