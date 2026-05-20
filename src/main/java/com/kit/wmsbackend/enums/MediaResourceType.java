package com.kit.wmsbackend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MediaResourceType {
    IMAGE("image"),
    RAW("raw"),
    VIDEO("video");

    private final String value;

    public static MediaResourceType fromValue(String value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(resourceType -> resourceType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown media resource type: " + value));
    }
}
