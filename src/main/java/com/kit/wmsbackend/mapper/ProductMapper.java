package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.feature.product.dto.ProductInfoRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ProductMapper {
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "isActive", defaultValue = "true")
    Product toProduct(ProductInfoRequest productInfoRequest);

    ProductInfoResponse toProductInfoResponse(Product product);
}
