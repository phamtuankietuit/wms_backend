package com.kit.wmsbackend.feature.attribute.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.dto.QuerySpecification;
import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.exception.ResourceAlreadyExistsException;
import com.kit.wmsbackend.exception.ResourceNotFoundException;
import com.kit.wmsbackend.feature.attribute.dto.AttributeListQueryFieldConfig;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.attribute.repository.AttributeRepository;
import com.kit.wmsbackend.mapper.AttributeMapper;
import com.kit.wmsbackend.mapper.ListResponseMapper;
import com.kit.wmsbackend.service.BaseQueryService;
import com.kit.wmsbackend.validator.SortValidator;
import com.kit.wmsbackend.utils.AttributeUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttributeServiceImpl extends BaseQueryService<Attribute> implements AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;
    private final AttributeValueSyncService attributeValueSyncService;
    private final SortValidator sortValidator;
    private final AttributeListQueryFieldConfig listQueryFieldConfig;
    private final QuerySpecification<Attribute> querySpecification;

    @Override
    public ListResponse<List<AttributeResponse>> list(@NonNull ListRequest listRequest) {
        return ListResponseMapper.toListResponse(
                search(
                    querySpecification,
                    sortValidator,
                    listQueryFieldConfig,
                    attributeRepository,
                    listRequest
                )
                        .map(attributeMapper::toAttributeResponse),
                listRequest.sort(),
                listRequest.filters()
        );
    }

    @Override
    @Transactional
    public AttributeResponse create(@NonNull AttributeRequest attributeRequest) {
        String normalizedCode = AttributeUtils.normalizeCode(attributeRequest.code());

        if (isCodeExists(normalizedCode)) {
            throw new ResourceAlreadyExistsException("Code already exists");
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
                .orElseThrow(() -> new ResourceNotFoundException("Attribute",  "id", id));

        String normalizedCode = AttributeUtils.normalizeCode(attributeRequest.code());

        if (normalizedCode != null && attributeRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new ResourceAlreadyExistsException("Code already exists");
        }

        attributeMapper.updateAttribute(attribute, attributeRequest);
        attribute.setCode(normalizedCode);
        attribute.setName(attributeRequest.name().trim());

        return attributeValueSyncService.upsert(attributeRequest, attribute);
    }

    @Override
    public boolean isCodeExists(String code) {
        String normalized = AttributeUtils.normalizeCode(code);
        return normalized != null && attributeRepository.existsByCode(normalized);
    }
}