package com.kit.wmsbackend.validator;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.exception.BadRequestException;
import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.attribute.repository.AttributeRepository;
import com.kit.wmsbackend.feature.attribute.specificationbuilder.AttributeSpecificationBuilder;
import com.kit.wmsbackend.feature.attributevalue.repository.AttributeValueRepository;
import com.kit.wmsbackend.feature.product.dto.*;
import com.kit.wmsbackend.feature.product.repository.ProductRepository;
import com.kit.wmsbackend.feature.variant.dto.VariantRequest;
import com.kit.wmsbackend.feature.variant.repository.VariantRepository;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCreateValidator {
    static final int MAX_ATTRIBUTES = 2;
    static final int MAX_VARIANT_COMBINATIONS = 100;

    ProductRepository productRepository;
    AttributeRepository attributeRepository;
    AttributeValueRepository attributeValueRepository;
    VariantRepository variantRepository;
    AttributeSpecificationBuilder attributeSpecificationBuilder;

    public ProductCreateValidationResult validate(@NonNull ProductCreateRequest productCreateRequest) {
        ProductInfoRequest productInfoRequest = productCreateRequest.productInfo();

        String normalizedCode = normalizeCode(productInfoRequest.code());

        if (productRepository.existsByCode(normalizedCode)) {
            throw new ResourceAlreadyExistsException("Product code already exists");
        }

        Map<UUID, ProductCreateAttributeContext> attributeContexts =
                validateAndLoadAttributes(productCreateRequest.attributes());
        List<ProductCreateAttributeContext> orderedAttributeContexts = List.copyOf(attributeContexts.values());
        validateCombinationCapacity(orderedAttributeContexts);
        Map<UUID, AttributeValue> selectedValuesById = loadAndValidateAttributeValues(attributeContexts);
        List<List<UUID>> combinations = generateCombinations(orderedAttributeContexts);
        Map<String, VariantRequest> variantRequestMap = mapVariantRequests(productCreateRequest.variants(), combinations);

        return new ProductCreateValidationResult(
                normalizedCode,
                productInfoRequest,
                List.copyOf(attributeContexts.values()),
                selectedValuesById,
                combinations,
                variantRequestMap
        );
    }

    private @NonNull String normalizeCode(String code) {
        String normalized = StringNormalizeUtils.normalizeCode(code);
        if (normalized == null) {
            throw new BadRequestException("Product code is required");
        }

        return normalized;
    }

    private @NonNull Map<UUID, ProductCreateAttributeContext> validateAndLoadAttributes(
            List<ProductAttributeRequest> productAttributeRequests
    ) {
        Map<UUID, ProductCreateAttributeContext> contexts = new LinkedHashMap<>();

        if (productAttributeRequests == null || productAttributeRequests.isEmpty()) {
            return contexts;
        }

        if (productAttributeRequests.size() > MAX_ATTRIBUTES) {
            throw new BadRequestException("At most 2 attributes are allowed");
        }

        Map<UUID, ProductAttributeRequest> requestsById = new LinkedHashMap<>();
        for (ProductAttributeRequest request : productAttributeRequests) {
            if (requestsById.putIfAbsent(request.attributeId(), request) != null) {
                throw new BadRequestException("Duplicate attribute in payload: " + request.attributeId());
            }
        }

        Set<UUID> attributeIds = requestsById.keySet();
        Specification<Attribute> spec = attributeSpecificationBuilder
                .notDeletedAndActiveByIds(attributeIds);
        List<Attribute> attributes = attributeRepository.findAll(spec);

        if (attributes.size() != attributeIds.size()) {
            Set<UUID> missing = new HashSet<>(attributeIds);
            attributes.stream()
                    .map(Attribute::getId)
                    .forEach(missing::remove);
            String missingIds = missing.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(", "));
            throw new ResourceNotFoundException("Attribute", "id", missingIds);
        }

        for (Attribute attribute : attributes) {
            ProductAttributeRequest request = requestsById.get(attribute.getId());
            contexts.put(attribute.getId(),
                    new ProductCreateAttributeContext(attribute, request.attributeValueIds()));
        }

        return contexts;
    }

    private void validateCombinationCapacity(List<ProductCreateAttributeContext> attributeContexts) {
        long expectedCombinations = 1L;

        for (ProductCreateAttributeContext context : attributeContexts) {
            expectedCombinations *= context.attributeValueIds().size();
            if (expectedCombinations > MAX_VARIANT_COMBINATIONS) {
                throw new BadRequestException("Generated variant combinations must not exceed 100");
            }
        }
    }

    private Map<UUID, AttributeValue> loadAndValidateAttributeValues(
            Map<UUID, ProductCreateAttributeContext> attributeContexts
    ) {
        Map<UUID, AttributeValue> selectedValues = new LinkedHashMap<>();

        for (ProductCreateAttributeContext context : attributeContexts.values()) {
            Set<UUID> uniqueIds = new LinkedHashSet<>(context.attributeValueIds());
            if (uniqueIds.size() != context.attributeValueIds().size()) {
                throw new BadRequestException("Duplicate attribute value ids for attribute: " + context.attribute().getId());
            }

            for (UUID valueId : uniqueIds) {
                AttributeValue attributeValue = attributeValueRepository.findNotDeletedById(valueId)
                        .orElseThrow(() -> new ResourceNotFoundException("AttributeValue", "id", valueId));

                if (!attributeValue.getAttribute().getId().equals(context.attribute().getId())) {
                    throw new BadRequestException("Attribute value does not belong to attribute: " + valueId);
                }

                if (!Boolean.TRUE.equals(attributeValue.getIsActive())) {
                    throw new BadRequestException("Attribute value is inactive: " + valueId);
                }

                selectedValues.put(attributeValue.getId(), attributeValue);
            }
        }

        return selectedValues;
    }

    private List<List<UUID>> generateCombinations(List<ProductCreateAttributeContext> attributeContexts) {
        return switch (attributeContexts.size()) {
            case 0 -> List.of(List.of());
            case 1 -> generateSingleAttributeCombinations(attributeContexts.get(0));
            case 2 -> generateTwoAttributeCombinations(attributeContexts.get(0), attributeContexts.get(1));
            default -> throw new BadRequestException("At most 2 attributes are allowed");
        };
    }

    private List<List<UUID>> generateSingleAttributeCombinations(ProductCreateAttributeContext attributeContext) {
        List<List<UUID>> combinations = new ArrayList<>(attributeContext.attributeValueIds().size());

        for (UUID valueId : attributeContext.attributeValueIds()) {
            combinations.add(List.of(valueId));
        }

        return combinations;
    }

    private List<List<UUID>> generateTwoAttributeCombinations(
            ProductCreateAttributeContext firstAttribute,
            ProductCreateAttributeContext secondAttribute
    ) {
        List<List<UUID>> combinations = new ArrayList<>(
                firstAttribute.attributeValueIds().size() * secondAttribute.attributeValueIds().size()
        );

        for (UUID firstValueId : firstAttribute.attributeValueIds()) {
            for (UUID secondValueId : secondAttribute.attributeValueIds()) {
                combinations.add(List.of(firstValueId, secondValueId));
            }
        }

        return combinations;
    }

    private Map<String, VariantRequest> mapVariantRequests(
            List<VariantRequest> variantRequests,
            List<List<UUID>> combinations
    ) {
        if (variantRequests == null || variantRequests.isEmpty()) {
            throw new BadRequestException("At least one variant is required");
        }

        if (variantRequests.size() != combinations.size()) {
            throw new BadRequestException("Variant count must match generated combinations");
        }

        Set<String> allowedCombinationKeys = combinations
                .stream()
                .map(this::combinationKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, VariantRequest> variantRequestMap = new LinkedHashMap<>();
        Set<String> seenSkus = new LinkedHashSet<>();

        for (VariantRequest request : variantRequests) {
            validateAndTrackSku(request, seenSkus);
            String combinationKey = validateAndGetCombinationKey(request, allowedCombinationKeys);

            if (variantRequestMap.putIfAbsent(combinationKey, request) != null) {
                throw new BadRequestException("Duplicate variant combination in payload: " + combinationKey);
            }
        }

        return variantRequestMap;
    }

    private void validateAndTrackSku(VariantRequest request, Set<String> seenSkus) {
        String sku = request.sku().trim();
        if (sku.isEmpty()) {
            throw new BadRequestException("SKU must not be blank");
        }

        if (!seenSkus.add(sku)) {
            throw new BadRequestException("Duplicate SKU in payload: " + sku);
        }

        if (variantRepository.existsBySku(sku)) {
            throw new ResourceAlreadyExistsException("SKU already exists: " + sku);
        }
    }

    private String validateAndGetCombinationKey(
            VariantRequest request,
            Set<String> allowedCombinationKeys
    ) {
        List<UUID> attributeValueIds = request.attributeValueIds() == null ? List.of() : request.attributeValueIds();
        String combinationKey = combinationKey(attributeValueIds);
        if (!allowedCombinationKeys.contains(combinationKey)) {
            throw new BadRequestException("Variant attribute values do not match generated combinations: " + combinationKey);
        }

        return combinationKey;
    }

    private String combinationKey(List<UUID> attributeValueIds) {
        StringBuilder builder = new StringBuilder();
        for (UUID attributeValueId : attributeValueIds) {
            if (!builder.isEmpty()) {
                builder.append('|');
            }
            builder.append(attributeValueId);
        }

        return builder.toString();
    }
}