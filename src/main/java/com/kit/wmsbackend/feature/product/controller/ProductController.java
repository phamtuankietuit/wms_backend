package com.kit.wmsbackend.feature.product.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.product.dto.ProductCheckCodeResponse;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @RequirePermission(PermissionCode.PRODUCT_CREATE)
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid
            @RequestBody
            ProductCreateRequest productRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(productRequest)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.PRODUCT_READ)
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/check-code")
    @RequirePermission(PermissionCode.PRODUCT_READ)
    public ResponseEntity<ApiResponse<ProductCheckCodeResponse>> checkCode(@RequestParam @NotBlank String code) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        new ProductCheckCodeResponse(
                                productService.isCodeExists(code)
                        )
                )
        );
    }
}
