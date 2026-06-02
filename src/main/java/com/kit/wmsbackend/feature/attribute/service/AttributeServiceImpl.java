package com.kit.wmsbackend.feature.attribute.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.attribute.dto.AttributeListQueryFieldConfig;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.attribute.repository.AttributeRepository;
import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.mapper.AttributeMapper;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeServiceImpl implements AttributeService {
    AttributeRepository attributeRepository;
    AttributeMapper attributeMapper;
    AttributeValueSyncService attributeValueSyncService;
    ListResponseAssembler listResponseAssembler;
    QueryService<Attribute> queryService;
    AttributeListQueryFieldConfig listQueryFieldConfig;

    @Override
    public ListResponse<List<AttributeResponse>> list(@NonNull ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
                queryService.list(listQueryFieldConfig, attributeRepository, listRequest)
                        .map(attributeMapper::toAttributeResponse)
        );
    }

    @Override
    @Transactional
    public AttributeResponse create(@NonNull AttributeRequest attributeRequest) {
        String normalizedCode = StringNormalizeUtils.normalizeCode(attributeRequest.code());

        if (isCodeExists(normalizedCode)) {
            throw new AppException(ErrorCode.ATTRIBUTE_CODE_ALREADY_EXISTS, normalizedCode);
        }

        Attribute attribute = attributeMapper.toAttribute(attributeRequest);
        attribute.setCode(normalizedCode);
        attribute.setName(attributeRequest.name().trim());

        return attributeValueSyncService.upsert(attributeRequest, attribute);
    }

    @Override
    @Transactional
    public AttributeResponse update(UUID id, @NonNull AttributeRequest attributeRequest) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND, id.toString()));

        String normalizedCode = StringNormalizeUtils.normalizeCode(attributeRequest.code());

        if (normalizedCode != null && attributeRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new AppException(ErrorCode.ATTRIBUTE_CODE_ALREADY_EXISTS, normalizedCode);
        }

        attributeMapper.updateAttribute(attribute, attributeRequest);
        attribute.setCode(normalizedCode);
        attribute.setName(attributeRequest.name().trim());

        return attributeValueSyncService.upsert(attributeRequest, attribute);
    }

    @Override
    public boolean isCodeExists(String code) {
        String normalized = StringNormalizeUtils.normalizeCode(code);
        return normalized != null && attributeRepository.existsByCode(normalized);
    }
}