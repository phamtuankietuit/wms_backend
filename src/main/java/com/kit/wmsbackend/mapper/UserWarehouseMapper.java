package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateMapper.class, UserMapper.class, WarehouseMapper.class})
public interface UserWarehouseMapper {
    @Mapping(target = "id.userId", source = "user.id")
    @Mapping(target = "id.warehouseId", source = "warehouse.id")
    UserWarehouse toEntity(User user, Warehouse warehouse);

    UserWarehouseResponse toUserWarehouseResponse(UserWarehouse userWarehouse);
}
