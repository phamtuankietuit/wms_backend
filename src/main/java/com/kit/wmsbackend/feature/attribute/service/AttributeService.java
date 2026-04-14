package com.kit.wmsbackend.feature.attribute.service;

import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface AttributeService {
    ListResponse<List<AttributeResponse>> list(@Valid ListRequest listRequest);
    AttributeResponse create(@Valid AttributeRequest attributeRequest);
    AttributeResponse update(UUID id, @Valid AttributeRequest attributeRequest);
    boolean isCodeExists(String code);
}
