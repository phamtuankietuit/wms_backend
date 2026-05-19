package com.kit.wmsbackend.feature.stocktransaction.comparator;

import com.kit.wmsbackend.feature.stocktransaction.dto.InventoryKey;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@NoArgsConstructor
public final class InventoryKeyComparator {
    public static final Comparator<InventoryKey> INVENTORY_LOCK_ORDER = Comparator
            .comparing(InventoryKey::warehouseId)
            .thenComparing(InventoryKey::variantId);
}
