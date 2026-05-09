package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.StockTransactionHistory;
import com.kit.wmsbackend.feature.stocktransactionhistory.dto.StockTransactionHistoryRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockTransactionHistoryMapper {
    StockTransactionHistory toEntity(StockTransactionHistoryRequest request);
}
