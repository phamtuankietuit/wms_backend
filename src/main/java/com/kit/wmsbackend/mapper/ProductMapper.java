package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.feature.product.dto.ProductCreateInfoRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.dto.list.ProductListResponse;
import com.kit.wmsbackend.feature.product.dto.update.ProductUpdateInfoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {DateMapper.class, AttributeMapper.class, ProductAttributeMapper.class}
)
public interface ProductMapper {
    @Mapping(target = "code", ignore = true)
    Product toProduct(ProductCreateInfoRequest productCreateInfoRequest);

    ProductResponse toProductResponse(Product product);

    ProductListResponse toProductListResponse(Product product);

    void updateProduct(@MappingTarget Product product, ProductUpdateInfoRequest productUpdateInfoRequest);

    ProductInfoResponse toProductInfoResponse(Product product);
}
