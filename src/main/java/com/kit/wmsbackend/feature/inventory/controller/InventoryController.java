package com.kit.wmsbackend.feature.inventory.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.annotation.RequirePermission;
import com.kit.wmsbackend.api.ApiResponse;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.enums.PermissionCode;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;
import com.kit.wmsbackend.feature.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
