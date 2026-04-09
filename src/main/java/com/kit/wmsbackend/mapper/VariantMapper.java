package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.feature.variant.dto.VariantRequest;
import com.kit.wmsbackend.feature.variant.dto.VariantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface VariantMapper {
    VariantResponse toVariantResponse(Variant variant);

    @Mapping(target = "isDefault", source = "isDefault")
    @Mapping(target = "isActive", source = "request.isActive", defaultValue = "true")
    Variant toVariant(Product product, VariantRequest request, boolean isDefault);

    List<VariantResponse> toVariantResponseList(List<Variant> variantList);
}
