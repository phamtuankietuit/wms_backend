package com.kit.wmsbackend.utils;

import java.util.Locale;

public class AttributeUtils {
    public static String normalizeCode(String code) {
        if (code == null) return null;
        String s = code.trim();
        return s.isEmpty() ? null : s.toUpperCase(Locale.ROOT);
    }
}
