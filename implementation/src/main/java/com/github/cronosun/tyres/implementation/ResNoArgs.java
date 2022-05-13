package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.ResInfo;

final class ResNoArgs<T> implements Res<T> {
    private static final Object[] NO_ARGS = new Object[] {};
    private final ResInfo resInfo;

    ResNoArgs(ResInfo resInfo) {
        this.resInfo = resInfo;
    }

    @Override
    public ResInfo info() {
        return resInfo;
    }

    @Override
    public Object[] args() {
        return NO_ARGS;
    }
}
