package com.kit.wmsbackend.feature.inventorymovement.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.InventoryMovement;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementListRequest;
import com.kit.wmsbackend.feature.inventorymovement.dto.InventoryMovementResponse;
import com.kit.wmsbackend.feature.inventorymovement.listqueryfieldconfig.InventoryMovementListQueryFieldConfig;
import com.kit.wmsbackend.feature.inventorymovement.repository.InventoryMovementRepository;
import com.kit.wmsbackend.mapper.InventoryMovementMapper;
import com.kit.wmsbackend.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryMovementServiceImpl implements InventoryMovementService {
    ListResponseAssembler listResponseAssembler;
    QueryService<InventoryMovement> queryService;
    InventoryMovementListQueryFieldConfig listQueryFieldConfig;
    InventoryMovementRepository inventoryMovementRepository;
    InventoryMovementMapper inventoryMovementMapper;

    @Override
    public ListResponse<List<InventoryMovementResponse>> listByInventoryId(
            UUID inventoryId,
            @NonNull InventoryMovementListRequest listRequest
    ) {
        List<FilterRequest> filters = new ArrayList<>();

        filters.add(new FilterRequest("inventory", "eq", inventoryId));

        ListRequest scopedListRequest = new ListRequest(
                filters,
                listRequest.search(),
                listRequest.pagination(),
                listRequest.sort()
        );

        return listResponseAssembler.toListResponse(
                queryService
                        .list(listQueryFieldConfig, inventoryMovementRepository, scopedListRequest)
                        .map(inventoryMovementMapper::toInventoryMovementResponse),
                listRequest.sort(),
                filters
        );
    }
}
