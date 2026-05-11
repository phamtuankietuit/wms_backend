package com.kit.wmsbackend.feature.inventory.listqueryfieldconfig;

import com.kit.wmsbackend.entity.*;
import com.kit.wmsbackend.interfaces.FilterStrategy;
import com.kit.wmsbackend.interfaces.ListQueryFieldConfig;
import com.kit.wmsbackend.interfaces.SearchStrategy;
import com.kit.wmsbackend.strategy.filter.*;
import com.kit.wmsbackend.strategy.search.LikeIgnoreCaseStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class InventoryListQueryFieldConfig implements ListQueryFieldConfig<Inventory> {
    private static final Map<String, SearchStrategy<Inventory>> SEARCHABLE_FIELDS = Map.of(
            "variantSku", new LikeIgnoreCaseStrategy<>(root -> root.join(Inventory_.variant).get(Variant_.sku)),
            "productCode", new LikeIgnoreCaseStrategy<>(root -> root.join(Inventory_.variant).join(Variant_.product).get(Product_.code)),
            "productName", new LikeIgnoreCaseStrategy<>(root -> root.join(Inventory_.variant).join(Variant_.product).get(Product_.name)),
            "warehouseCode", new LikeIgnoreCaseStrategy<>(root -> root.join(Inventory_.warehouse).get(Warehouse_.code)),
            "warehouseName", new LikeIgnoreCaseStrategy<>(root -> root.join(Inventory_.warehouse).get(Warehouse_.name))
    );

    private static final Map<String, Function<Root<Inventory>, Path<?>>> SORTABLE_FIELDS = Map.of(
            "quantity", root -> root.get(Inventory_.quantity),
            "reservedQuantity", root -> root.get(Inventory_.reservedQuantity),
            "createdAt", root -> root.get(BaseAuditEntity_.createdAt),
            "updatedAt", root -> root.get(BaseAuditEntity_.updatedAt),
            "variantSku", root -> root.join(Inventory_.variant).get(Variant_.sku),
            "warehouseName", root -> root.join(Inventory_.warehouse).get(Warehouse_.name)
    );

    private static final Map<String, Map<String, FilterStrategy<Inventory>>> FILTERABLE_FIELDS = Map.of(
            "warehouse", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(Inventory_.warehouse).get(BaseEntity_.id))
            ),
            "variant", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.join(Inventory_.variant).get(BaseEntity_.id))
            ),
            "quantity", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Inventory_.quantity)),
                    "gt or eq", new GreaterThanOrEqualToFilterStrategy<>(root -> root.get(Inventory_.quantity)),
                    "lt or eq", new LessThanOrEqualToFilterStrategy<>(root -> root.get(Inventory_.quantity))
            ),
            "reservedQuantity", Map.of(
                    "eq", new EqualsFilterStrategy<>(root -> root.get(Inventory_.reservedQuantity)),
                    "gt or eq", new GreaterThanOrEqualToFilterStrategy<>(root -> root.get(Inventory_.reservedQuantity)),
                    "lt or eq", new LessThanOrEqualToFilterStrategy<>(root -> root.get(Inventory_.reservedQuantity))
            ),
            "createdAt", Map.of(
                    "gt or eq", new GreaterThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt)),
                    "lt or eq", new LessThanOrEqualToFilterStrategy<>(root -> root.get(BaseAuditEntity_.createdAt))
            )
    );

    @Override
    public Map<String, SearchStrategy<Inventory>> searchableFields() {
        return SEARCHABLE_FIELDS;
    }

    @Override
    public Map<String, Function<Root<Inventory>, Path<?>>> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    public Map<String, Map<String, FilterStrategy<Inventory>>> filterableFields() {
        return FILTERABLE_FIELDS;
    }
}
