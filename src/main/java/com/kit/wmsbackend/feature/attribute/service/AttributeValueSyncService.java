package com.kit.wmsbackend.feature.attribute.service;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.attribute.repository.AttributeRepository;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueRequest;
import com.kit.wmsbackend.mapper.AttributeMapper;
import com.kit.wmsbackend.mapper.AttributeValueMapper;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeValueSyncService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;
    private final AttributeValueMapper attributeValueMapper;

    @Transactional
    public AttributeResponse upsert(
            @NonNull AttributeRequest attributeRequest,
            @NonNull Attribute attribute
    ) {
        Map<String, AttributeValueRequest> attributeValueRequestMap = validateAndNormalizeRequestValues(attributeRequest);
        Map<UUID, AttributeValue> existingValueByIdMap = mapExistingValuesById(attribute);
        Set<UUID> requestedIds = collectRequestedIds(attributeRequest);

        removeMissingValues(attribute, requestedIds);
        attributeRepository.saveAndFlush(attribute);
        mergeValues(attribute, attributeValueRequestMap, existingValueByIdMap);

        Attribute saved = attributeRepository.save(attribute);
        return attributeMapper.toAttributeResponse(saved);
    }

    private @NonNull Set<UUID> collectRequestedIds(@NonNull AttributeRequest attributeRequest) {
        Set<UUID> requestIds = new HashSet<>();
        for (AttributeValueRequest req : attributeRequest.attributeValues()) {
            UUID id = req.id();
            if (id == null) {
                continue;
            }

            if (!requestIds.add(id)) {
                throw new AppException(ErrorCode.ATTRIBUTE_VALUE_DUPLICATE, id.toString());
            }
        }

        return requestIds;
    }

    private @NonNull Map<UUID, AttributeValue> mapExistingValuesById(@NonNull Attribute attribute) {
        Map<UUID, AttributeValue> existingValueByIdMap = new LinkedHashMap<>();
        for (AttributeValue existing : attribute.getAttributeValues()) {
            if (existing.getId() != null) {
                existingValueByIdMap.put(existing.getId(), existing);
            }
        }

        return existingValueByIdMap;
    }

    private void removeMissingValues(
            @NonNull Attribute attribute,
            @NonNull Set<UUID> requestedIds
    ) {
        List<AttributeValue> existingValues = attribute.getAttributeValues().stream().toList();
        for (AttributeValue existing : existingValues) {
            UUID existingId = existing.getId();

            if (existingId != null && !requestedIds.contains(existingId)) {
                attribute.removeAttributeValue(existing);
            }
        }
    }

    private @NonNull Map<String, AttributeValueRequest> validateAndNormalizeRequestValues(
            @NonNull AttributeRequest attributeRequest
    ) {
        Map<String, AttributeValueRequest> attributeValueRequestMap = new LinkedHashMap<>();
        for (var v : attributeRequest.attributeValues()) {
            String code = StringNormalizeUtils.normalizeCode(v.code());
            if (code == null) {
                throw new AppException(ErrorCode.ATTRIBUTE_VALUE_CODE_REQUIRED);
            }

            if (attributeValueRequestMap.containsKey(code)) {
                throw new AppException(ErrorCode.ATTRIBUTE_VALUE_CODE_DUPLICATE, code);
            }

            attributeValueRequestMap.put(code, v);
        }

        return attributeValueRequestMap;
    }

    private void mergeValues(
            @NonNull Attribute attribute,
            @NonNull Map<String, AttributeValueRequest> attributeValueRequestMap,
            @NonNull Map<UUID, AttributeValue> existingValueByIdMap
    ) {
        for (AttributeValueRequest req : attributeValueRequestMap.values()) {
            String code = StringNormalizeUtils.normalizeCode(req.code());
            UUID requestId = req.id();

            if (requestId != null) {
                AttributeValue existing = existingValueByIdMap.get(requestId);
                if (existing == null) {
                    throw new AppException(ErrorCode.ATTRIBUTE_VALUE_NOT_BELONG_TO, requestId.toString());
                }

                attributeValueMapper.updateAttributeValue(existing, req);
                existing.setCode(code);
                existing.setValue(req.value().trim());
            } else {
                AttributeValue newVal = attributeValueMapper.toAttributeValue(req);
                newVal.setCode(code);
                newVal.setValue(req.value().trim());
                attribute.addAttributeValue(newVal);
            }
        }
    }
}
