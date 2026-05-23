package com.kit.wmsbackend.feature.media.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CloudinaryAttribute {
    BYTES("bytes"),
    DELETED("deleted"),
    FOLDER("folder"),
    FORMAT("format"),
    HEIGHT("height"),
    INVALIDATE("invalidate"),
    OVERWRITE("overwrite"),
    PUBLIC_ID("public_id"),
    RESOURCE_TYPE("resource_type"),
    SECURE_URL("secure_url"),
    TRANSFORMATION("transformation"),
    TYPE("type"),
    WIDTH("width");

    String key;
}
