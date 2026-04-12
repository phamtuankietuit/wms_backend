package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.ProductAttribute;
import com.kit.wmsbackend.feature.product.dto.ProductAttributeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AttributeMapper.class})
public interface ProductAttributeMapper {
    ProductAttributeResponse toProductAttributeResponse(ProductAttribute productAttribute);
}
