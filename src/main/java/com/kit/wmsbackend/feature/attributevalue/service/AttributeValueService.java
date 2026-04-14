package com.kit.wmsbackend.feature.attributevalue.service;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface AttributeValueService {
    Boolean isCodeExists(@RequestParam String code, @RequestParam UUID attributeId);
}
