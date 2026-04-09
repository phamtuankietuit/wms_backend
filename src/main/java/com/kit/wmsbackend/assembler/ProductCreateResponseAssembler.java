package com.kit.wmsbackend.assembler;

import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueResponse;
import com.kit.wmsbackend.feature.product.dto.ProductCreateAttributeContext;
import com.kit.wmsbackend.feature.product.dto.ProductCreateResponse;
import com.kit.wmsbackend.mapper.AttributeValueMapper;
import com.kit.wmsbackend.mapper.DateMapper;
import com.kit.wmsbackend.mapper.ProductMapper;
import com.kit.wmsbackend.mapper.VariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductCreateResponseAssembler {
    private final ProductMapper productMapper;
    private final VariantMapper variantMapper;
    private final AttributeValueMapper attributeValueMapper;
    private final DateMapper dateMapper;

    public ProductCreateResponse toProductCreateResponse(
        Product savedProduct,
        List<ProductCreateAttributeContext> attributeContexts,
        Map<UUID, AttributeValue> selectedValuesById,
        List<Variant> variants
    ) {
        return new ProductCreateResponse(
            productMapper.toProductInfoResponse(savedProduct),
            toAttributeResponses(attributeContexts, selectedValuesById),
            variantMapper.toVariantResponseList(variants)
        );
    }

    private List<AttributeResponse> toAttributeResponses(
        List<ProductCreateAttributeContext> attributeContexts,
        Map<UUID, AttributeValue> selectedValuesById
    ) {
        return attributeContexts
            .stream()
            .map(context -> toAttributeResponse(context, selectedValuesById))
            .toList();
    }

    private AttributeResponse toAttributeResponse(
        ProductCreateAttributeContext context,
        Map<UUID, AttributeValue> selectedValuesById
    ) {
        List<UUID> attributeValueIds = context.attributeValueIds() == null ? List.of() : context.attributeValueIds();

        return new AttributeResponse(
            context.attribute().getId(),
            context.attribute().getCode(),
            context.attribute().getName(),
            context.attribute().getIsActive(),
            dateMapper.toOffsetDateTime(context.attribute().getCreatedAt()),
            dateMapper.toOffsetDateTime(context.attribute().getUpdatedAt()),
            attributeValueIds.stream()
                .map(valueId -> toAttributeValueResponse(valueId, selectedValuesById))
                .toList()
        );
    }

    private AttributeValueResponse toAttributeValueResponse(
        UUID attributeValueId,
        Map<UUID, AttributeValue> selectedValuesById
    ) {
        AttributeValue attributeValue = Objects.requireNonNull(
            selectedValuesById.get(attributeValueId),
            "AttributeValue not found in selectedValuesById for id: " + attributeValueId
        );

        return attributeValueMapper.toAttributeValueResponse(attributeValue);
    }
}