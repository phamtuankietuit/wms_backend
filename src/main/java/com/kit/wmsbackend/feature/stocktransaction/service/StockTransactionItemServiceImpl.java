package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.StockTransactionItem;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemListRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemResponse;
import com.kit.wmsbackend.feature.stocktransaction.listqueryfieldconfig.StockTransactionItemListQueryFieldConfig;
import com.kit.wmsbackend.feature.stocktransaction.repository.StockTransactionItemRepository;
import com.kit.wmsbackend.mapper.StockTransactionItemMapper;
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
public class StockTransactionItemServiceImpl implements StockTransactionItemService {
    ListResponseAssembler listResponseAssembler;
    QueryService<StockTransactionItem> queryService;
    StockTransactionItemListQueryFieldConfig listQueryFieldConfig;
    StockTransactionItemRepository stockTransactionItemRepository;
    StockTransactionItemMapper stockTransactionItemMapper;

    @Override
    public ListResponse<List<StockTransactionItemResponse>> listByStockTransactionId(
            @NonNull UUID stockTransactionId,
            @NonNull StockTransactionItemListRequest request
    ) {
        List<FilterRequest> filters = new ArrayList<>();

        filters.add(new FilterRequest("stockTransactionId", "eq", stockTransactionId));

        ListRequest scopedListRequest = new ListRequest(
                filters,
                request.search(),
                request.pagination(),
                request.sort()
        );

        return listResponseAssembler.toListResponse(
                queryService
                        .list(
                                listQueryFieldConfig,
                                stockTransactionItemRepository,
                                scopedListRequest
                        )
                        .map(stockTransactionItemMapper::toStockTransactionItemResponse)
        );
    }
}
