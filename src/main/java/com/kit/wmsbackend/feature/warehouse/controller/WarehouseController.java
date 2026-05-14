package com.kit.wmsbackend.feature.warehouse.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import com.kit.wmsbackend.feature.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping("/list")
    @RequirePermission(PermissionCode.WAREHOUSE_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<WarehouseResponse>>>> list(
            @Valid @RequestBody ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.list(listRequest)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.WAREHOUSE_READ)
    public ResponseEntity<ApiResponse<WarehouseResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.getById(id)));
    }

    @PostMapping
    @RequirePermission(PermissionCode.WAREHOUSE_CREATE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(@Valid @RequestBody WarehouseRequest warehouseRequest) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.create(warehouseRequest)));
    }

    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.WAREHOUSE_UPDATE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseRequest warehouseRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.update(id, warehouseRequest)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.WAREHOUSE_DELETE)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        warehouseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/restore")
    @RequirePermission(PermissionCode.WAREHOUSE_UPDATE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> restore(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.restore(id)));
    }
}

