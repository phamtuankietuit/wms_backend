package com.kit.wmsbackend.feature.warehouse.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Warehouse;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseListQueryConfig;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseRequest;
import com.kit.wmsbackend.feature.warehouse.dto.WarehouseResponse;
import com.kit.wmsbackend.feature.warehouse.repository.WarehouseRepository;
import com.kit.wmsbackend.mapper.WarehouseMapper;
import com.kit.wmsbackend.service.QueryService;
import com.kit.wmsbackend.utils.SecurityUtils;
import com.kit.wmsbackend.utils.StringNormalizeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseServiceImpl implements WarehouseService {
    WarehouseRepository warehouseRepository;
    WarehouseMapper warehouseMapper;
    QueryService<Warehouse> queryService;
    ListResponseAssembler listResponseAssembler;
    WarehouseListQueryConfig listQueryConfig;


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
        Warehouse warehouse = warehouseRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, id.toString()));

        String normalizedCode = StringNormalizeUtils.normalizeCode(warehouseRequest.code());

        if (warehouseRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new AppException(ErrorCode.WAREHOUSE_CODE_ALREADY_EXISTS, normalizedCode);
        }

        warehouseMapper.updateWarehouse(warehouse, warehouseRequest);

        return warehouseMapper.toWarehouseResponse(warehouse);
    }

    @Override
    public WarehouseResponse getById(UUID id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toWarehouseResponse)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, id.toString()));
    }

    @Override
    public ListResponse<List<WarehouseResponse>> list(ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
                queryService.list(listQueryConfig, warehouseRepository, listRequest)
                        .map(warehouseMapper::toWarehouseResponse)
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Warehouse warehouse = warehouseRepository.findNotDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, id.toString()));

        UUID currentUserId = SecurityUtils.getCurrentUserIdOrSystem("delete warehouse");
        warehouseRepository.softDelete(warehouse, currentUserId);
    }

    @Override
    @Transactional
    public WarehouseResponse restore(UUID id) {
        Warehouse warehouse = warehouseRepository.findDeletedById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, id.toString()));

        return warehouseMapper.toWarehouseResponse(warehouseRepository.restore(warehouse));
    }
}
