package com.kit.wmsbackend.feature.inventorymovement.listqueryfieldconfig;

import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.EqualsFilterStrategy;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class InventoryMovementListQueryFieldConfig implements ListQueryFieldConfig<InventoryMovement> {
    private static final Map<String, SearchStrategy<InventoryMovement>> SEARCHABLE_FIELDS = Map.of(
            "stockTransactionCode", new LikeIgnoreCaseStrategy<>(root -> root.join(InventoryMovement_.stockTransaction).get(StockTransaction_.code))
    );

    private static final Map<String, Function<Root<InventoryMovement>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "quantityChange", root -> root.get(InventoryMovement_.quantityChange),
            "beforeQuantity", root -> root.get(InventoryMovement_.beforeQuantity),
            "afterQuantity", root -> root.get(InventoryMovement_.afterQuantity),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt)
    );

    private static final Map<String, Map<String, FilterStrategy<InventoryMovement>>> FILTERABLE_FIELDS = Map.of(
            "inventory", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(InventoryMovement_.inventory).get(BaseEntity_.id))
            )
    );

    @Override
    public Map<String, SearchStrategy<InventoryMovement>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<InventoryMovement>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<InventoryMovement>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
