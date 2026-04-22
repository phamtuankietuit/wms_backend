package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.StockTransactionItem;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemResponse;
import com.kit.wmsbackend.feature.stocktransaction.dto.StockTransactionItemResult;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface StockTransactionItemMapper {
    StockTransactionItem toStockTransactionItem(StockTransactionItemResult result);
    Collection<StockTransactionItem> toStockTransactionItems(Collection<StockTransactionItemResult> results);
    StockTransactionItemResponse toStockTransactionItemResponse(StockTransactionItem stockTransactionItem);
}
