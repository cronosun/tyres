package com.github.cronosun.tyres.implementation;

import java.nio.charset.StandardCharsets;

/**
 * Wrap the: We must not be able to use identity (to make it more realistic).
 */
public class Key {
    private final String value;

    public Key(String value) {
        this.value = value;
    }

    public Key getClone() {
        return new Key(clone(value));
    }

    public String getValue() {
        return value;
    }

    private static String clone(String string) {
        var utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Key) {
            var key = (Key)o;
            return value.equals(key.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
