package com.kit.wmsbackend.utils;

import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import com.kit.wmsbackend.enums.ErrorCode;
import com.kit.wmsbackend.exception.AppException;

@NoArgsConstructor
public final class CriteriaValueConverter {
    public static Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }

        if (targetType.isEnum() && value instanceof String stringValue) {
            return convertEnum(targetType, stringValue);
        }

        if (isLongType(targetType)) {
            return convertLong(value);
        }

        if (isIntegerType(targetType)) {
            return convertInteger(value);
        }

        if (isDoubleType(targetType)) {
            return convertDouble(value);
        }

        if (isFloatType(targetType)) {
            return convertFloat(value);
        }

        if (isBooleanType(targetType)) {
            return convertBoolean(value);
        }

        if (isUuidType(targetType)) {
            return convertUuid(value);
        }

        if (isInstantType(targetType)) {
            return convertInstant(value);
        }

        return value.toString();
    }

    private static @NonNull Object convertEnum(@NonNull Class<?> targetType, String stringValue) {
        try {
            return Enum.valueOf(targetType.asSubclass(Enum.class), stringValue);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid enum value: " + stringValue);
        }
    }

    private static boolean isLongType(Class<?> targetType) {
        return targetType == Long.class || targetType == long.class;
    }

    private static boolean isIntegerType(Class<?> targetType) {
        return targetType == Integer.class || targetType == int.class;
    }

    private static boolean isDoubleType(Class<?> targetType) {
        return targetType == Double.class || targetType == double.class;
    }

    private static boolean isFloatType(Class<?> targetType) {
        return targetType == Float.class || targetType == float.class;
    }

    private static boolean isBooleanType(Class<?> targetType) {
        return targetType == Boolean.class || targetType == boolean.class;
    }

    private static boolean isUuidType(Class<?> targetType) {
        return targetType == UUID.class;
    }

    private static boolean isInstantType(Class<?> targetType) {
        return targetType == Instant.class;
    }

    private static Object convertLong(Object value) {
        try {
            return value instanceof Number number ? number.longValue() : Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid long value: " + value);
        }
    }

    private static Object convertInteger(Object value) {
        try {
            return value instanceof Number number ? number.intValue() : Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid integer value: " + value);
        }
    }

    private static Object convertDouble(Object value) {
        try {
            return value instanceof Number number ? number.doubleValue() : Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid double value: " + value);
        }
    }

    private static Object convertFloat(Object value) {
        try {
            return value instanceof Number number ? number.floatValue() : Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid float value: " + value);
        }
    }

    private static Object convertBoolean(Object value) {
        return value instanceof Boolean booleanValue ? booleanValue : Boolean.valueOf(value.toString());
    }

    private static Object convertUuid(Object value) {
        try {
            return value instanceof UUID uuid ? uuid : UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid UUID value: " + value);
        }
    }

    private static Object convertInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }

        if (value instanceof String text) {
            try {
                return Instant.parse(text);
            } catch (DateTimeParseException e) {
                throw new AppException(ErrorCode.DATA_PARSE_ERROR, "Invalid instant value: " + text);
            }
        }

        throw new AppException(ErrorCode.FILTER_INVALID_VALUE, "Expected Instant: " + value);
    }
}
