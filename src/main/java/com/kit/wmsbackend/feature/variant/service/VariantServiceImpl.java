package com.kit.wmsbackend.feature.variant.service;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.feature.variant.dto.VariantResponse;
import com.kit.wmsbackend.feature.variant.repository.VariantRepository;
import com.kit.wmsbackend.mapper.VariantMapper;
import com.kit.wmsbackend.service.BaseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl extends BaseQueryService<Variant> implements VariantService {
    private final VariantMapper variantMapper;
    private final VariantRepository variantRepository;


    public VariantResponse getVariantById(UUID id) {

        Variant variant = variantRepository.findNotDeletedById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + id));

        return variantMapper.toVariantResponse(variant);
    }
}
