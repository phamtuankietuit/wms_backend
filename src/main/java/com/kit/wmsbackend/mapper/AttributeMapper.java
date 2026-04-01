package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AttributeValueMapper.class, DateMapper.class})
public interface AttributeMapper {
    @Mapping(target = "attributeValues", ignore = true)
    @Mapping(target = "isActive", defaultValue = "true")
    Attribute toAttribute(AttributeRequest attributeRequest);

    @Mapping(target = "attributeValues", ignore = true)
    @Mapping(target = "isActive", defaultValue = "true")
    void updateAttribute(@MappingTarget Attribute attribute, AttributeRequest attributeRequest);

    AttributeResponse toAttributeResponse(Attribute attribute);
}
