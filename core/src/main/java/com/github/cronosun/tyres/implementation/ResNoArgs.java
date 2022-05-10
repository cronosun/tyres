package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.Res;
import com.github.cronosun.tyres.ResInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

final class ResNoArgs<T> implements Res<T>, ResInfo {
    private static final Object[] NO_ARGS = new Object[] {};
    private final Method method;
    private final String name;
    private final BaseInfo baseInfo;

    ResNoArgs(Method method, String name, BaseInfo baseInfo) {
        this.method = method;
        this.name = name;
        this.baseInfo = baseInfo;
    }

    @Override
    public Class<?> bundleClass() {
        return baseInfo.getResourceClass();
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public Object[] args() {
        return NO_ARGS;
    }

    @Override
    public String name() {
        return name;
    }

    @Nullable
    @Override
    public List<String> customPackage() {
        return baseInfo.getCustomPackage();
    }

    @Override
    public ResInfo info() {
        return this;
    }
}
