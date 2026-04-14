package com.kit.wmsbackend.feature.attributevalue.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.feature.attributevalue.dto.AttributeValueCheckCodeResponse;
import com.kit.wmsbackend.feature.attributevalue.service.AttributeValueService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/attribute-values")
@Validated
@RequiredArgsConstructor
public class AttributeValueController {
    private final AttributeValueService attributeValueService;

    @GetMapping("/check-code")
    public ResponseEntity<ApiResponse<AttributeValueCheckCodeResponse>> checkCode(
            @RequestParam @NotBlank String code,
            @RequestParam UUID attributeId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        new AttributeValueCheckCodeResponse(
                                attributeValueService.isCodeExists(code, attributeId)
                        )
                )
        );
    }
}
