package com.kit.wmsbackend.feature.userwarehouse.service;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.UserWarehouseId;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.userwarehouse.repository.UserWarehouseRepository;
import com.kit.wmsbackend.mapper.UserWarehouseMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserWarehouseServiceImpl implements UserWarehouseService {
    UserWarehouseRepository userWarehouseRepository;
    UserWarehouseMapper userWarehouseMapper;

    @Override
    @Transactional
    public void assign(User user, @NonNull Collection<Warehouse> warehouses) {
        if (warehouses.isEmpty()) {
            return;
        }

        List<UserWarehouse> userWarehouses = warehouses
                .stream()
                .map(warehouse -> userWarehouseMapper.toEntity(user, warehouse))
                .toList();

        userWarehouseRepository.saveAll(userWarehouses);
    }

    @Override
    @Transactional
    public void unassign(User user, @NonNull Collection<Warehouse> warehouses) {
        if  (warehouses.isEmpty()) {
            return;
        }

        List<UserWarehouseId> ids = warehouses
                .stream()
                .map(warehouse -> new UserWarehouseId(user.getId(), warehouse.getId()))
                .toList();

        List<UserWarehouse> existing = userWarehouseRepository.findAllById(ids);

        userWarehouseRepository.deleteAll(existing);
    }
}
