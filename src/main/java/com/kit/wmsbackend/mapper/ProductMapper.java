package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.feature.product.dto.ProductCreateInfoRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateMapper.class, AttributeMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {
    @Mapping(target = "code", ignore = true)
    Product toProduct(ProductCreateInfoRequest productCreateInfoRequest);

    ProductInfoResponse toProductInfoResponse(Product product);

    ProductResponse toProductResponse(Product product);
}
