package com.kit.wmsbackend.feature.attribute.repository;

import com.kit.wmsbackend.entity.Attribute;
import com.kit.wmsbackend.repository.BaseAuditRepository;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface AttributeRepository extends BaseAuditRepository<Attribute> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, @NonNull UUID id);
    boolean existsById(@NonNull UUID id);
}
