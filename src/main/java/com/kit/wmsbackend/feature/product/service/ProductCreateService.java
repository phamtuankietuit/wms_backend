package com.kit.wmsbackend.feature.product.service;

import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.entity.Product;
import com.kit.wmsbackend.entity.ProductAttribute;
import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.entity.VariantAttributeValue;
import com.kit.wmsbackend.feature.product.assembler.ProductAttributeAssembler;
import com.kit.wmsbackend.feature.product.dto.*;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.feature.product.util.CombinationKeyUtils;
import com.kit.wmsbackend.feature.variant.dto.VariantRequest;
import com.kit.wmsbackend.mapper.*;
import com.kit.wmsbackend.validator.ProductCreateValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCreateService {
    ProductRepository productRepository;
    ProductCreateValidator productCreateValidator;
    ProductAttributeAssembler productAttributeAssembler;
    ProductMapper productMapper;
    VariantMapper variantMapper;
    VariantAttributeValueMapper variantAttributeValueMapper;

    @Transactional
    public ProductResponse create(ProductCreateRequest productRequest) {
        ProductCreateValidationResult validation = productCreateValidator.validate(productRequest);

        Product product = productMapper.toProduct(validation.productInfo());
        product.setCode(validation.normalizedCode());
        product.setName(product.getName().trim());
        product.setIsActive(false);

        buildProductAttributes(product, validation.attributeContexts());

        buildVariants(
                product,
                validation.combinations(),
                validation.variantRequestMap(),
                validation.selectedValuesById()
        );

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);
    }

    private void buildProductAttributes(
            Product product, 
            @NonNull List<ProductCreateAttributeContext> contexts
    ) {
        for (ProductCreateAttributeContext context : contexts) {
            ProductAttribute productAttribute = productAttributeAssembler.toProductAttribute(product, context);
            product.addProductAttribute(productAttribute);
        }
    }

    private void buildVariants(
            Product product,
            @NonNull List<List<UUID>> combinations,
            Map<String, VariantRequest> variantRequestMap,
            Map<UUID, AttributeValue> selectedValuesById
    ) {
        for (List<UUID> combination : combinations) {
            VariantRequest request = variantRequestMap.get(CombinationKeyUtils.canonicalCombinationKey(combination));
            if (request == null) {
                throw new IllegalStateException("Missing variant payload for combination: " + combination);
            }

            Variant variant = variantMapper.toVariant(product, request, combinations.size() == 1);
            buildVariantAttributeValues(variant, request, selectedValuesById);
            product.addVariant(variant);
        }
    }

    private void buildVariantAttributeValues(
            Variant variant,
            @NonNull VariantRequest request,
            Map<UUID, AttributeValue> selectedValuesById
    ) {
        List<UUID> attributeValueIds = request.attributeValueIds() == null ? List.of() : request.attributeValueIds();

        for (UUID attributeValueId : attributeValueIds) {
            AttributeValue attributeValue = selectedValuesById.get(attributeValueId);

            if (attributeValue == null) {
                throw new IllegalStateException("Unknown attribute value in variant payload: " + attributeValueId);
            }

            VariantAttributeValue variantAttributeValue = variantAttributeValueMapper.toVariantAttributeValue(
                    variant,
                    attributeValue
            );

            variant.addVariantAttributeValue(variantAttributeValue);
        }
    }
}