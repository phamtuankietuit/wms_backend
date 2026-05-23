package com.kit.wmsbackend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MediaResourceTypeConverter implements AttributeConverter<MediaResourceType, String> {
    @Override
    public String convertToDatabaseColumn(MediaResourceType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public MediaResourceType convertToEntityAttribute(String dbData) {
        return MediaResourceType.fromValue(dbData);
    }
}
