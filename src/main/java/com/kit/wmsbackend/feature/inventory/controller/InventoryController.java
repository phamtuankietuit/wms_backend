package com.kit.wmsbackend.feature.inventory.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;
import com.kit.wmsbackend.feature.inventory.service.InventoryService;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementListRequest;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiPrefix
@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
@Validated
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/list")
    @RequirePermission(PermissionCode.INVENTORY_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<InventoryResponse>>>> list(
            @Valid @RequestBody ListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.list(listRequest)));
    }

    @PostMapping("/{id}/movements")
    @RequirePermission(PermissionCode.INVENTORY_READ)
    public ResponseEntity<ApiResponse<ListResponse<List<InventoryMovementResponse>>>> listMovements(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryMovementListRequest listRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.listMovements(id, listRequest)));
    }

    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.INVENTORY_READ)
    public ResponseEntity<ApiResponse<InventoryResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getById(id)));
    }
}
