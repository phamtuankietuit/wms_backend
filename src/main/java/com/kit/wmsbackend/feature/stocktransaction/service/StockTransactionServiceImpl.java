package com.kit.wmsbackend.feature.stocktransaction.service;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.entity.StockTransactionItem;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionRequest;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResult;
import com.kit.wmsbackend.feature.stocktransaction.repository.StockTransactionRepository;
import com.kit.wmsbackend.mapper.StockTransactionItemMapper;
import com.kit.wmsbackend.mapper.StockTransactionMapper;
import com.kit.wmsbackend.validator.StockTransactionCreateValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockTransactionServiceImpl implements StockTransactionService {
    StockTransactionCreateValidator validator;
    StockTransactionMapper stockTransactionMapper;
    StockTransactionItemMapper stockTransactionItemMapper;
    StockTransactionRepository stockTransactionRepository;

    @Override
    @Transactional
    public StockTransactionResponse create(StockTransactionRequest stockTransactionRequest) {
        StockTransactionResult result = validator.validate(stockTransactionRequest);

        StockTransaction stockTransaction = stockTransactionMapper.toStockTransaction(result);
        List<StockTransactionItem> stockTransactionItems = stockTransactionItemMapper
                .toStockTransactionItems(result.items())
                .stream()
                .toList();


        stockTransaction.addStockTransactionItems(stockTransactionItems);

        StockTransaction saved = stockTransactionRepository.save(stockTransaction);

        return stockTransactionMapper.toStockTransactionResponse(saved);
    }
}
