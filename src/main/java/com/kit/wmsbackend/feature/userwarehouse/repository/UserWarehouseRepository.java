package com.kit.wmsbackend.feature.userwarehouse.repository;

import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.UserWarehouseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, UserWarehouseId> {
    List<UserWarehouse> findAllByIdUserId(UUID userId);

    List<UserWarehouse> findAllByIdWarehouseId(UUID warehouseId);
}
