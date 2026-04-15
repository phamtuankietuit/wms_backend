package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import com.kit.wmsbackend.mapper.WarehouseMapper;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    @Transactional
    public WarehouseResponse create(@NonNull WarehouseRequest warehouseRequest) {
        String normalizedCode = StringNormalizeUtils.normalizeCode(warehouseRequest.code());

        if (warehouseRepository.existsByCode(normalizedCode)) {
            throw new AppException(ErrorCode.WAREHOUSE_CODE_ALREADY_EXISTS, normalizedCode);
        }

        Warehouse warehouse = warehouseMapper.toWarehouse(warehouseRequest);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toWarehouseResponse(savedWarehouse);
    }

    @Override
    @Transactional
    public WarehouseResponse update(UUID id, @NonNull WarehouseRequest warehouseRequest) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, id.toString()));

        String normalizedCode = StringNormalizeUtils.normalizeCode(warehouseRequest.code());

        if (warehouseRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new AppException(ErrorCode.WAREHOUSE_CODE_ALREADY_EXISTS, normalizedCode);
        }

        warehouseMapper.updateWarehouse(warehouse, warehouseRequest);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toWarehouseResponse(savedWarehouse);
    }
}
