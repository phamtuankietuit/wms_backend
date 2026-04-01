package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.feature.product.dto.ProductRequest;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "isActive", source = "isActive", defaultValue = "true")
    Product toProduct(ProductRequest productRequest);
}
