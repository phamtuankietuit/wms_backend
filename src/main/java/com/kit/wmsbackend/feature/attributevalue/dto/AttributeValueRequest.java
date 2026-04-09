package com.kit.wmsbackend.feature.attributevalue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AttributeValueRequest(
        UUID id,

        @NotBlank(message = "Attribute value code is required")
        @Size(max = 100)
        String code,

        @NotBlank(message = "Value is required")
        @Size(max = 100)
        String value,

        Boolean isActive
) {
}
