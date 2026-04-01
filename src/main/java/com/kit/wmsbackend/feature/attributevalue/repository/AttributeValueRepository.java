package com.kit.wmsbackend.feature.attributevalue.repository;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.entity.AttributeValue;
import com.kit.wmsbackend.repository.BaseAuditRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeValueRepository extends BaseAuditRepository<AttributeValue> {
    Boolean existsByCodeAndAttribute_Id(String code, UUID attributeId);
}
