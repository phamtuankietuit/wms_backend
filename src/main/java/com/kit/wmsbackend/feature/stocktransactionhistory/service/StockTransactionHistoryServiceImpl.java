package com.kit.wmsbackend.feature.stocktransactionhistory.service;

import com.kit.wmsbackend.entity.StockTransactionHistory;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import com.kit.wmsbackend.feature.stocktransactionhistory.repository.StockTransactionHistoryRepository;
import com.kit.wmsbackend.mapper.StockTransactionHistoryMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionHistoryServiceImpl implements StockTransactionHistoryService {
    StockTransactionHistoryRepository stockTransactionHistoryRepository;
    StockTransactionHistoryMapper stockTransactionHistoryMapper;

    @Override
    @Transactional
    public void logHistory(@NonNull StockTransactionHistoryRequest request) {
        StockTransactionHistory history = stockTransactionHistoryMapper.toEntity(request);
        request.stockTransaction().getStockTransactionHistories().add(history);
        stockTransactionHistoryRepository.save(history);
    }
}
