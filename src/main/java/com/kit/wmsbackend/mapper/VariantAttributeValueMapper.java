package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.entity.VariantAttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VariantAttributeValueMapper {
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "id.variantId", source = "variant.id")
    @Mapping(target = "id.attributeValueId", source = "attributeValue.id")
    VariantAttributeValue toVariantAttributeValue(Variant variant, AttributeValue attributeValue);
}
