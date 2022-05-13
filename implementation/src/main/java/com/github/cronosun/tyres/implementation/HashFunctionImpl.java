package com.github.cronosun.tyres.implementation;

import java.lang.reflect.Method;

final class HashFunctionImpl implements TrMap.HashFunction<Method> {

    static final int HASH_START = 142543232;
    static final HashFunctionImpl INSTANCE = new HashFunctionImpl();

    public HashFunctionImpl() {
    }

    @Override
    public int hash(Method method) {
        var hash = HASH_START;
        hash = hash ^ method.getDeclaringClass().getName().hashCode();
        hash = 7 * hash + method.getParameterCount();

        // add name
        var name = method.getName();
        int length = name.length() >> 1;
        for (int i = 0; i < length; i++) {
            hash = 31 * hash + name.charAt(i);
        }

        return hash;
    }
}
