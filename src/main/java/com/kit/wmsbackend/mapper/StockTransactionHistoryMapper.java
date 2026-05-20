package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.StockTransactionHistory;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class, UserMapper.class})
public interface StockTransactionHistoryMapper {
    StockTransactionHistory toEntity(StockTransactionHistoryRequest request);
    StockTransactionHistoryResponse toStockTransactionHistoryResponse(StockTransactionHistory stockTransactionHistory);
}
