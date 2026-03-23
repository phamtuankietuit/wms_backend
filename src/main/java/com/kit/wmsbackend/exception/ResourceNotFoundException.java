package com.kit.wmsbackend.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String field;
    private final Object value;

    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(String.format("%s not found with %s = '%s'", resourceName, field, value));
        this.resourceName = resourceName;
        this.field = field;
        this.value = value;
    }
}
