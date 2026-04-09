package com.kit.wmsbackend.feature.product.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductCreateResponse;
import com.kit.wmsbackend.feature.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiPrefix
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @RequirePermission(PermissionCode.PRODUCT_CREATE)
    public ResponseEntity<ApiResponse<ProductCreateResponse>> create(
            @Valid
            @RequestBody
            ProductCreateRequest productRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(productRequest)));
    }
}
