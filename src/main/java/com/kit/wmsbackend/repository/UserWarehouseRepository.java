package com.kit.wmsbackend.repository;

import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.UserWarehouseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, UserWarehouseId> {
    List<UserWarehouse> findAllByIdUserId(String userId);

    List<UserWarehouse> findAllByIdWarehouseId(String warehouseId);
}
