package com.kit.wmsbackend.feature.userwarehouse.controller;

import com.kit.wmsbackend.annotation.ApiPrefix;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseAssignRequest;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import com.kit.wmsbackend.feature.userwarehouse.service.UserWarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiPrefix
@RestController
@RequestMapping("/user-warehouses")
@RequiredArgsConstructor
public class UserWarehouseController {
    private final UserWarehouseService userWarehouseService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserWarehouseResponse>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userWarehouseService.findByUserId(userId));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<List<UserWarehouseResponse>> findByWarehouseId(@PathVariable String warehouseId) {
        return ResponseEntity.ok(userWarehouseService.findByWarehouseId(warehouseId));
    }

    @PostMapping("/assign")
    public ResponseEntity<UserWarehouseResponse> assign(@Valid @RequestBody UserWarehouseAssignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userWarehouseService.assign(request.userId(), request.warehouseId()));
    }

    @DeleteMapping("/{userId}/{warehouseId}")
    public ResponseEntity<Void> unassign(@PathVariable String userId, @PathVariable String warehouseId) {
        userWarehouseService.unassign(userId, warehouseId);
        return ResponseEntity.noContent().build();
    }
}
