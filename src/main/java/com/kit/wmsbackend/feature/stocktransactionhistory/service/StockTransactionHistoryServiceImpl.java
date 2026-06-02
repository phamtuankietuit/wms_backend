package com.kit.wmsbackend.feature.stocktransactionhistory.service;

import com.kit.wmsbackend.assembler.ListResponseAssembler;
import com.kit.wmsbackend.dto.FilterRequest;
import com.kit.wmsbackend.dto.ListRequest;
import com.kit.wmsbackend.dto.ListResponse;
import com.kit.wmsbackend.entity.StockTransactionHistory;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryResponse;
import com.kit.wmsbackend.feature.stocktransactionhistory.listqueryfieldconfig.StockTransactionHistoryListQueryFieldConfig;
import com.kit.wmsbackend.feature.stocktransactionhistory.repository.StockTransactionHistoryRepository;
import com.kit.wmsbackend.mapper.StockTransactionHistoryMapper;
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
public class StockTransactionHistoryServiceImpl implements StockTransactionHistoryService {
    StockTransactionHistoryRepository stockTransactionHistoryRepository;
    StockTransactionHistoryMapper stockTransactionHistoryMapper;
    ListResponseAssembler listResponseAssembler;
    QueryService<StockTransactionHistory> queryService;
    StockTransactionHistoryListQueryFieldConfig listQueryFieldConfig;

    @Override
    @Transactional
    public void logHistory(@NonNull StockTransactionHistoryRequest request) {
        StockTransactionHistory history = stockTransactionHistoryMapper.toEntity(request);
        request.stockTransaction().getStockTransactionHistories().add(history);
        stockTransactionHistoryRepository.save(history);
    }

    @Override
    public ListResponse<List<StockTransactionHistoryResponse>> list(UUID stockTransactionId, @NonNull ListRequest listRequest) {
        List<FilterRequest> filters = new ArrayList<>();

        FilterRequest filterRequest = new FilterRequest(
                "stockTransaction",
                "eq",
                stockTransactionId
        );

        filters.add(filterRequest);
        if (listRequest.filters() != null) {
            filters.addAll(listRequest.filters());
        }

        ListRequest scopedListRequest = new ListRequest(
                filters,
                listRequest.search(),
                listRequest.pagination(),
                listRequest.sort()
        );

        return listResponseAssembler.toListResponse(
                queryService
                        .list(
                                listQueryFieldConfig,
                                stockTransactionHistoryRepository,
                                scopedListRequest,
                                false,
                                true)
                        .map(stockTransactionHistoryMapper::toStockTransactionHistoryResponse)
        );
    }
}
