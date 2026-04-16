package com.kit.wmsbackend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    SERVER_INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SERVER_RESOURCE_NOT_FOUND(404, "Server resource not found"),

    TOKEN_HASHING_ERROR(500, "An error occurred while processing the token"),

    JWT_INVALID_OR_EXPIRED_TOKEN(401, "Invalid or expired token"),

    VALIDATION_ERROR(500, "Validation error"),
    VALIDATION_FAILED(400, "Validation failed"),

    HTTP_MESSAGE_NOT_READABLE(400, "Malformed JSON request"),

    AUTH_FORBIDDEN(403, "Forbidden"),
    AUTH_UNAUTHORIZED(401, "Unauthorized"),

    RESOURCE_NOT_FOUND(404, "Resource not found:"),

    FILTER_INVALID_FIELD(400, "Invalid filter field:"),
    FILTER_INVALID_OPERATOR(400, "Invalid filter operator:"),
    FILTER_INVALID_VALUE_FOR_FIELD(400, "Invalid filter value for this field with operator:"),
    FILTER_INVALID_VALUE(400, "Invalid filter value:"),

    SORT_FIELD_REQUIRED(400, "Sort field cannot be empty"),
    SORT_INVALID_FIELD(400, "Invalid sort field:"),
    SORT_DIRECTION_REQUIRED(400, "Sort direction cannot be empty"),
    SORT_INVALID_DIRECTION(400, "Sort direction must be 'asc' or 'desc'. Invalid:"),

    WAREHOUSE_NOT_FOUND(404, "Warehouse not found"),
    WAREHOUSE_CODE_INVALID(400, "Warehouse code is invalid"),
    WAREHOUSE_CODE_ALREADY_EXISTS(409, "Warehouse code already exists"),

    PRODUCT_NOT_FOUND(404, "Product not found with:"),
    PRODUCT_ALREADY_EXISTS(409, "Product already exists"),
    PRODUCT_CODE_ALREADY_EXISTS(409, "Product code already exists"),
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

    ATTRIBUTE_NOT_FOUND(404, "Attribute not found with:"),
    ATTRIBUTE_CODE_ALREADY_EXISTS(409, "Attribute code already exists"),

    ATTRIBUTE_VALUE_NOT_FOUND(404, "Attribute value not found with:"),
    ATTRIBUTE_VALUE_INACTIVE(400, "Attribute value is inactive:"),
    ATTRIBUTE_VALUE_DUPLICATE(400, "Attribute value duplicated"),
    ATTRIBUTE_VALUE_CODE_REQUIRED(400, "Attribute value code is required"),
    ATTRIBUTE_VALUE_CODE_DUPLICATE(400, "Attribute value code duplicated"),
    ATTRIBUTE_VALUE_NOT_BELONG_TO(400, "Attribute value not belong to"),

    PERMISSION_NOT_FOUND(404, "Permission not found with:"),
    PERMISSION_CODE_ALREADY_EXISTS(409, "Permission code already exists:"),

    PERMISSION_GROUP_NOT_FOUND(404, "Permission group not found with:"),

    USER_NOT_FOUND(404, "User not found with:"),
    USER_EMAIL_ALREADY_EXISTS(409, "User email already exists:");

    int status;
    String message;
}
