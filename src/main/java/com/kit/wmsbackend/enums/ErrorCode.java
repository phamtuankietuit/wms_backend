package com.kit.wmsbackend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    AUTH_FORBIDDEN(403, "Forbidden"),

    FILTER_INVALID_FIELD(400, "Invalid filter field:"),
    FILTER_INVALID_OPERATOR(400, "Invalid filter operator:"),
    FILTER_INVALID_VALUE_FOR_FIELD(400, "Invalid filter value for this field with operator:"),
    FILTER_INVALID_VALUE(400, "Invalid filter value:"),

    SORT_FIELD_REQUIRED(400, "Sort field cannot be empty"),
    SORT_INVALID_FIELD(400, "Invalid sort field:"),
    SORT_DIRECTION_REQUIRED(400, "Sort direction cannot be empty"),
    SORT_INVALID_DIRECTION(400, "Sort direction must be 'asc' or 'desc'. Invalid:"),

    WAREHOUSE_NOT_FOUND(404, "Warehouse not found"),
    WAREHOUSE_CODE_ALREADY_EXISTS(409, "Warehouse code already exists"),

    PRODUCT_NOT_FOUND(404, "Product not found with"),
    PRODUCT_ALREADY_EXISTS(409, "Product already exists"),
    PRODUCT_FIELD_REQUIRED(400, "Product field is required:"),
    PRODUCT_MAX_ATTRIBUTE(400, "Maximum number of attributes allowed is 2"),
    PRODUCT_ATTRIBUTE_DUPLICATE(400, "Duplicate attribute"),
    PRODUCT_ATTRIBUTE_VALUE_DUPLICATE(400, "Duplicate attribute value for the same attribute:"),
    PRODUCT_ATTRIBUTE_VALUE_NOT_MATCH_ATTRIBUTE(404, "Attribute value does not belong to attribute:"),
    PRODUCT_MAX_VARIANT(400, "Maximum number of variant combination is 100"),
    PRODUCT_MIN_VARIANT(400, "Minimum number of variant combination is 1"),
    PRODUCT_VARIANT_COUNT_NOT_MATCH(400, "Variant count not match"),
    PRODUCT_VARIANT_DUPLICATE(400, "Variant duplicated"),
    PRODUCT_VARIANT_INVALID_COMBINATION(400, "Invalid variant combination"),

    VARIANT_SKU_ALREADY_EXIST(409, "Variant sku already exists"),

    ATTRIBUTE_NOT_FOUND(404, "Attribute not found"),

    ATTRIBUTE_VALUE_INACTIVE(400, "Attribute value is inactive:"),

    INTERNAL_SERVER_ERROR(500, "Internal server error");

    int status;
    String message;
}
