package com.kit.wmsbackend.feature.userwarehouse.service;

import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;

import java.util.List;

public interface UserWarehouseService {
    List<UserWarehouseResponse> findByUserId(String userId);

    List<UserWarehouseResponse> findByWarehouseId(String warehouseId);

    UserWarehouseResponse assign(String userId, String warehouseId);

    void unassign(String userId, String warehouseId);
}
