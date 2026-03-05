package com.kit.wmsbackend.feature.userwarehouse.service;

import com.kit.wmsbackend.entity.User;
import com.kit.wmsbackend.entity.UserWarehouse;
import com.kit.wmsbackend.entity.UserWarehouseId;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.feature.user.repository.UserRepository;
import com.kit.wmsbackend.feature.userwarehouse.dto.UserWarehouseResponse;
import com.kit.wmsbackend.feature.userwarehouse.repository.UserWarehouseRepository;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWarehouseServiceImpl implements UserWarehouseService {
    private final UserWarehouseRepository userWarehouseRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<UserWarehouseResponse> findByUserId(UUID userId) {
        return userWarehouseRepository.findAllByIdUserId(userId.toString())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<UserWarehouseResponse> findByWarehouseId(UUID warehouseId) {
        return userWarehouseRepository.findAllByIdWarehouseId(warehouseId.toString())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserWarehouseResponse assign(UUID userId, UUID warehouseId) {
        UserWarehouseId id = new UserWarehouseId(userId, warehouseId);
        if (userWarehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("User is already assigned to this warehouse");
        }

        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId.toString())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + warehouseId));

        UserWarehouse userWarehouse = new UserWarehouse();
        userWarehouse.setId(id);
        userWarehouse.setUser(user);
        userWarehouse.setWarehouse(warehouse);

        return toResponse(userWarehouseRepository.save(userWarehouse));
    }

    @Transactional
    @Override
    public void unassign(UUID userId, UUID warehouseId) {
        UserWarehouseId id = new UserWarehouseId(userId, warehouseId);
        UserWarehouse existing = userWarehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User-warehouse assignment not found"));
        userWarehouseRepository.delete(existing);
    }

    private UserWarehouseResponse toResponse(UserWarehouse userWarehouse) {
        return new UserWarehouseResponse(
                userWarehouse.getId().getUserId(),
                userWarehouse.getId().getWarehouseId(),
                userWarehouse.getAssignedAt()
        );
    }
}
