package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.StockTransaction;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class, StockTransactionItemMapper.class, UserMapper.class})
public interface StockTransactionMapper {
    StockTransaction toStockTransaction(StockTransactionResult result);
    StockTransactionResponse toStockTransactionResponse(StockTransaction stockTransaction);
}
