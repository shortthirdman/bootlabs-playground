package com.shortthirdman.bootlabs.batchjobs.core.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemPropertyUtils {

    public static <T> T getProperty(String key, T defaultValue, Class<T> type) {
        String rawValue = System.getProperty(key);
        if (rawValue == null || rawValue.isBlank()) {
            rawValue = System.getenv(key);
        }

        if (rawValue == null || rawValue.isBlank()) {
            return defaultValue;
        }

        try {
            Object parsed = parseValue(rawValue, type);
            return type.cast(parsed);
        } catch (Exception e) {
            log.warn("Invalid value for key '{}': '{}'. Using default '{}'", key, rawValue, defaultValue);
            return defaultValue;
        }
    }

    private static Object parseValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        }

        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        }

        if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        }

        if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        }

        if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        }

        throw new IllegalArgumentException("Unsupported type: " + type.getSimpleName());
    }
}
