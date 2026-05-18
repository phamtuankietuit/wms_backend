package com.kit.wmsbackend.service;

import com.kit.wmsbackend.entity.BaseEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderedFetchService {
    public <T extends BaseEntity> @NonNull List<T> fetchInOrder(
            @NonNull List<UUID> ids,
            @NonNull Function<List<UUID>, List<T>> fetcher
    ) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<UUID, T> entitiesById = fetcher
                .apply(ids)
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity(), (existing, ignored) -> existing));

        return ids.stream()
                .map(entitiesById::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
