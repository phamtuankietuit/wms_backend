package com.kit.wmsbackend.feature.product.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.product.dto.ProductCheckCodeResponse;
import com.kit.wmsbackend.feature.product.dto.ProductCreateRequest;
import com.kit.wmsbackend.feature.product.dto.ProductInfoResponse;
import com.kit.wmsbackend.feature.product.dto.ProductResponse;
import com.kit.wmsbackend.feature.product.dto.list.ProductListResponse;
import com.kit.wmsbackend.feature.product.dto.update.ProductUpdateInfoRequest;
import com.kit.wmsbackend.feature.product.service.ProductService;
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

    @PostMapping("/list")
    @RequirePermission(PermissionCode.PRODUCT_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<ProductListResponse>>>> list(
            @Valid @RequestBody ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.list(listRequest)));
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

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<ProductInfoResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateInfoRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.PRODUCT_DELETE)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.softDeleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/restore")
    @RequirePermission(PermissionCode.PRODUCT_RESTORE)
    public ResponseEntity<ApiResponse<ProductResponse>> restore(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.restoreById(id)));
    }
}
