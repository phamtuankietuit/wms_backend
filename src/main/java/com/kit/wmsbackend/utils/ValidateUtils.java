package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.entity.BaseEntity;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ValidateUtils {
    public <T extends BaseEntity> void validateEntitiesExist(
            @NonNull Collection<T> found,
            @NonNull Collection<UUID> requestedIds,
            ErrorCode errorCode
    ) {
        if (found.size() != requestedIds.size()) {
            Set<UUID> foundIds = found.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = requestedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw new AppException(errorCode,
                    String.join(", ", missingIds.stream().map(UUID::toString).toList()));
        }
    }
}
