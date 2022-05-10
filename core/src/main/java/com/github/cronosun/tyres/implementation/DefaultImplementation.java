package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.TyResImplementation;

import java.lang.reflect.Proxy;

public final class DefaultImplementation implements TyResImplementation {

    private static final TyResImplementation INSTANCE = new DefaultImplementation();

    public static TyResImplementation instance() {
        return INSTANCE;
    }

    @Override
    public <T> T createInstance(Class<T> bundleClass) {
        //noinspection unchecked
        return  (T) Proxy.newProxyInstance(
                bundleClass.getClassLoader(),
                new Class[] {bundleClass},
                new TyResInvocationHandler(bundleClass));
    }
}
