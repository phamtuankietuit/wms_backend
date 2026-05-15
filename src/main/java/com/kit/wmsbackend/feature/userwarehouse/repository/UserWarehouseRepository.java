package com.kit.wmsbackend.feature.userwarehouse.repository;

import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.UserWarehouseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, UserWarehouseId> {
    @Query("""
            SELECT uw
            FROM UserWarehouse uw
            JOIN FETCH uw.warehouse w
            JOIN FETCH uw.user u
            WHERE u.id = :userId
            """)
    List<UserWarehouse> findAllByUserId(@Param("userId") UUID userId);
}
