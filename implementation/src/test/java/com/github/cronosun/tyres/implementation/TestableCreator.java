package com.github.cronosun.tyres.implementation;

import java.util.*;

public final class TestableCreator {

    private TestableCreator() {
    }

    public static Testable createTestableForTyMap(int numberOfEntries) {
        var keys = generateKeys(numberOfEntries);
        var entries = keys.stream().map(key -> {
            var clonedKey = key.getClone();
            var value = new Value(clonedKey.getValue());
            return new TrMap.Entry<>(key, value);
        });
        var map = TrMap.createInstance(Key::hashCode, numberOfEntries * 3, entries);
        return new Testable() {
            @Override
            public int numberOfEntries() {
                return numberOfEntries;
            }

            @Override
            public Key getKey(int index) {
                return keys.get(index);
            }

            @Override
            public Value getValue(Key key) {
                return map.get(key);
            }
        };
    }

    public static Testable generateTestableForHashMap(int numberOfEntries) {
        var keys = generateKeys(numberOfEntries);
        var map = new HashMap<Key, Value>(numberOfEntries);
        for (var key : keys) {
            var clonedKey = key.getClone();
            map.put(clonedKey, new Value(key.getValue()));
        }
        return new Testable() {
            @Override
            public int numberOfEntries() {
                return numberOfEntries;
            }

            @Override
            public Key getKey(int index) {
                return keys.get(index);
            }

            @Override
            public Value getValue(Key key) {
                return map.get(key);
            }
        };
    }

    private static ArrayList<Key> generateKeys(int numberOfEntries) {
        var keys = new ArrayList<Key>(numberOfEntries);
        for (var index = 0; index<numberOfEntries; index++) {
            var key = new Key(generateStringForIndex(index));
            keys.add(key);
        }
        return keys;
    }

    private static String generateStringForIndex(int index) {
        final String format;
        if (index%2==0) {
            format = "thisIsSomeKey_%d_%d_end";
        } else {
            format = "anotherKey_%d_someMore";
        }
        return String.format(format, index, index % 3 * 31);
    }

}
