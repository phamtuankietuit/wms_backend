package com.kit.wmsbackend.exception;

public class InvalidFilterException extends BadRequestException {
    private InvalidFilterException(String message) {
        super(message);
    }

    public static InvalidFilterException invalidField(String field) {
        return new InvalidFilterException("Invalid filter field: " + field);
    }

    public static InvalidFilterException invalidOperator(String operator, String field) {
        return new InvalidFilterException("Invalid filter operator: " + operator + " for field: " + field);
    }

    public static InvalidFilterException invalidValue(String field, String operator) {
        return new InvalidFilterException("Invalid filter value for field: " + field + " with operator: " + operator);
    }

    public static InvalidFilterException invalidValueMessage(String message) {
        return new InvalidFilterException(message);
    }
}