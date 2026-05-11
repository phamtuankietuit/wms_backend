package com.kit.wmsbackend.feature.inventory.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.Inventory;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import com.kit.wmsbackend.feature.inventory.dto.InventoryResponse;
import com.kit.wmsbackend.feature.inventory.listqueryfieldconfig.InventoryListQueryFieldConfig;
import com.kit.wmsbackend.feature.inventory.repository.InventoryRepository;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementListRequest;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import com.kit.wmsbackend.feature.inventorymovement.service.InventoryMovementService;
import com.kit.wmsbackend.mapper.InventoryMapper;
import com.kit.wmsbackend.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService{
    InventoryRepository inventoryRepository;
    QueryService<Inventory> queryService;
    InventoryMapper inventoryMapper;
    ListResponseAssembler listResponseAssembler;
    InventoryListQueryFieldConfig listQueryFieldConfig;
    InventoryMovementService inventoryMovementService;

    @Override
    public boolean isAvailableQuantity(UUID inventoryId, Integer quantity) {
        Inventory inventory = inventoryRepository.findNotDeletedById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND, inventoryId.toString()));

        return inventory.getAvailableQuantity() >= quantity;
    }

    @Override
    public Long getAvailableQuantity(UUID variantId, UUID warehouseId) {
        Inventory inventory = inventoryRepository
                .findByVariantIdAndWarehouseIdAndDeletedAtIsNull(variantId, warehouseId);

        if (inventory == null) {
            return 0L;
        } else {
            return inventory.getAvailableQuantity();
        }
    }

    @Override
    public ListResponse<List<InventoryResponse>> list(ListRequest listRequest) {
        return listResponseAssembler.toListResponse(
                queryService.list(
                        listQueryFieldConfig,
                        inventoryRepository,
                        listRequest,
                        false,
                        false
                ).map(inventoryMapper::toInventoryResponse),
                listRequest.sort(),
                listRequest.filters()
        );
    }

    @Override
    public InventoryResponse getById(UUID inventoryId) {
        return inventoryMapper.toInventoryResponse(
                inventoryRepository
                        .findNotDeletedById(inventoryId)
                        .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND, inventoryId.toString()))
        );
    }

    @Override
    public ListResponse<List<InventoryMovementResponse>> listMovements(
            UUID inventoryId,
            InventoryMovementListRequest listRequest
    ) {
        if (inventoryRepository.existsById(inventoryId)) {
            return inventoryMovementService.listByInventoryId(inventoryId, listRequest);
        }

        throw new AppException(ErrorCode.INVENTORY_NOT_FOUND, inventoryId.toString());
    }
}
