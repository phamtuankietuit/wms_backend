package com.kit.wmsbackend.feature.userwarehouse.service;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.Warehouse;

import java.util.Collection;

public interface UserWarehouseService {
    void assign(User user, Collection<Warehouse> warehouses);

    void unassign(User user, Collection<Warehouse> warehouses);
}
