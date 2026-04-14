package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueRequest;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface AttributeValueMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    @Mapping(target = "isActive", defaultValue = "true")
    AttributeValue toAttributeValue(AttributeValueRequest attributeValueRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    @Mapping(target = "isActive", defaultValue = "true")
    void updateAttributeValue(@MappingTarget AttributeValue attributeValue, AttributeValueRequest attributeValueRequest);

    AttributeValueResponse toAttributeValueResponse(AttributeValue attributeValue);
}
