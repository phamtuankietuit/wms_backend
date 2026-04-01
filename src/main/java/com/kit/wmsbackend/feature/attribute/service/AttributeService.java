package com.kit.wmsbackend.feature.attribute.service;

import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface AttributeService {
    AttributeResponse create(@Valid AttributeRequest attributeRequest);
    AttributeResponse update(UUID id, @Valid AttributeRequest attributeRequest);
    boolean isCodeExists(String code);
}
