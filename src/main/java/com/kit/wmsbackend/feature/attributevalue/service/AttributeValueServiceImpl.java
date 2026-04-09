package com.kit.wmsbackend.feature.attributevalue.service;

import com.kit.wmsbackend.feature.attributevalue.repository.AttributeValueRepository;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttributeValueServiceImpl implements AttributeValueService {
    private final AttributeValueRepository attributeValueRepository;

    @Override
    public Boolean isCodeExists(@NonNull String code, UUID attributeId) {
        return attributeValueRepository.existsByCodeAndAttribute_Id(
                StringNormalizeUtils.normalizeCode(code),
                attributeId
        );
    }
}
