package com.kit.wmsbackend.feature.userwarehouse.service;

import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;

import java.util.List;
import java.util.UUID;

public interface UserWarehouseService {
    List<UserWarehouseResponse> findByUserId(UUID userId);

    List<UserWarehouseResponse> findByWarehouseId(UUID warehouseId);

    UserWarehouseResponse assign(UUID userId, UUID warehouseId);

    void unassign(UUID userId, UUID warehouseId);
}
