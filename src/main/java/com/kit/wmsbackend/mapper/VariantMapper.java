package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.feature.variant.dto.VariantResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VariantMapper {
    VariantResponse toVariantResponse(Variant variant);
}
