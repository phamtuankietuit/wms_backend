package com.kit.wmsbackend.feature.product.util;

import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public final class CombinationKeyUtils {
    public static @NonNull String canonicalCombinationKey(List<UUID> attributeValueIds) {
        if (attributeValueIds == null || attributeValueIds.isEmpty()) {
            return "";
        }

        List<UUID> orderedIds = attributeValueIds.stream()
                .sorted()
                .toList();

        StringBuilder builder = new StringBuilder();
        for (UUID attributeValueId : orderedIds) {
            if (!builder.isEmpty()) {
                builder.append('|');
            }
            builder.append(attributeValueId);
        }

        return builder.toString();
    }
}