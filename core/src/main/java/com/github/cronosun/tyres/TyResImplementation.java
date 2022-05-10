package com.github.cronosun.tyres;

public interface TyResImplementation {
    <T> T createInstance(Class<T> bundleClass);
}
