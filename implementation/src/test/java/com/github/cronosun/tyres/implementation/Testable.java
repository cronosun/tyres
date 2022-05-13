package com.github.cronosun.tyres.implementation;

public interface Testable {
    int numberOfEntries();
    Key getKey(int index);
    Value getValue(Key key);
}
