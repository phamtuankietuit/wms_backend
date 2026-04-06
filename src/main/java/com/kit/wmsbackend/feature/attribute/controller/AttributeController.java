package com.kit.wmsbackend.feature.attribute.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.attribute.dto.AttributeCheckCodeResponse;
import com.kit.wmsbackend.feature.attribute.dto.AttributeRequest;
import com.kit.wmsbackend.feature.attribute.dto.AttributeResponse;
import com.kit.wmsbackend.feature.attribute.service.AttributeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/attributes")
@Validated
@RequiredArgsConstructor
public class AttributeController {
    private final AttributeService attributeService;

    @PostMapping("/list")
    @RequirePermission(PermissionCode.ATTRIBUTE_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<AttributeResponse>>>> list(
            @Valid @RequestBody ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(attributeService.list(listRequest)));
    }

    @PostMapping
    @RequirePermission(PermissionCode.ATTRIBUTE_CREATE)
    public ResponseEntity<ApiResponse<AttributeResponse>> create(
            @Valid @RequestBody AttributeRequest attributeRequest
    ) {
            return ResponseEntity.ok(ApiResponse.success(attributeService.create(attributeRequest)));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.ATTRIBUTE_UPDATE)
    public ResponseEntity<ApiResponse<AttributeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AttributeRequest attributeRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(attributeService.update(id, attributeRequest)));
    }

    @GetMapping("/check-code")
    @RequirePermission(PermissionCode.ATTRIBUTE_READ)
    public ResponseEntity<ApiResponse<AttributeCheckCodeResponse>> checkCode(@RequestParam @NotBlank String code) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        new AttributeCheckCodeResponse(
                                attributeService.isCodeExists(code)
                        )
                )
        );
    }
}
