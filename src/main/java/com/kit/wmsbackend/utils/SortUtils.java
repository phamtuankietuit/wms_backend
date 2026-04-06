package com.kit.wmsbackend.utils;

import com.kit.wmsbackend.exception.BadRequestException;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;

import java.util.Locale;
import java.util.Set;

@NoArgsConstructor
public final class SortUtils {
    public static final Set<String> VALID_DIRECTIONS = Set.of("asc", "desc");

    public static @NonNull String normalizeDirection(@NonNull String direction) {
        return direction.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isValidDirection(@NonNull String direction) {
        return VALID_DIRECTIONS.contains(normalizeDirection(direction));
    }

    public static Sort.@NonNull Direction toSpringDirection(@NonNull String direction) {
        String normalized = normalizeDirection(direction);
        if (!VALID_DIRECTIONS.contains(normalized)) {
            throw new BadRequestException("Invalid sort direction: " + direction + ". Must be 'asc' or 'desc'");
        }
        return Sort.Direction.fromString(normalized);
    }
}
