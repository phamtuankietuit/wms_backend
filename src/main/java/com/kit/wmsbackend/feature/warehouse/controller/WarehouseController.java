package com.kit.wmsbackend.feature.warehouse.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import com.kit.wmsbackend.feature.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiPrefix
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController {
    private final WarehouseService warehouseService;

//    @GetMapping
//    @RequirePermission(PermissionCode.WAREHOUSE_READ)
//    public ResponseEntity<List<Warehouse>> findAll() {
//        return ResponseEntity.ok(warehouseService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    @RequirePermission(PermissionCode.WAREHOUSE_READ)
//    public ResponseEntity<Warehouse> findById(@PathVariable UUID id) {
//        return ResponseEntity.ok(warehouseService.findById(id));
//    }

    @PostMapping
    @RequirePermission(PermissionCode.WAREHOUSE_CREATE)
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(@Valid @RequestBody WarehouseRequest warehouseRequest) {
        return ResponseEntity.ok(ApiResponse.success(warehouseService.create(warehouseRequest)));
    }

//    @PutMapping("/{id}")
//    @RequirePermission(PermissionCode.WAREHOUSE_UPDATE)
//    public ResponseEntity<Warehouse> update(@PathVariable UUID id, @RequestBody Warehouse warehouse) {
//        return ResponseEntity.ok(warehouseService.update(id, warehouse));
//    }
//
//    @DeleteMapping("/{id}")
//    @RequirePermission(PermissionCode.WAREHOUSE_DELETE)
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        warehouseService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}

