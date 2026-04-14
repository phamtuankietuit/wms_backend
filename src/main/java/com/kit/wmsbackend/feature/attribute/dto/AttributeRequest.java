package com.kit.wmsbackend.feature.attribute.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AttributeRequest(
        @NotBlank(message = "Code is required")
        @Size(max = 100)
        String code,

        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        Boolean isActive,

        @NotNull
        List<@Valid AttributeValueRequest> attributeValues
) {
}
