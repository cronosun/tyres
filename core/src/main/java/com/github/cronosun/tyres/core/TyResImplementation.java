package com.github.cronosun.tyres.core;

public interface TyResImplementation {
    <T> T createInstance(Class<T> bundleClass);
}
