package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.Res;
import com.github.cronosun.tyres.ResInfo;

final class ResWithArgs<T> implements Res<T> {

    private final ResInfo resInfo;
    private final Object[] args;

    ResWithArgs(ResInfo resInfo, Object[] args) {
        this.resInfo = resInfo;
        this.args = args;
    }

    @Override
    public ResInfo info() {
        return resInfo;
    }

    @Override
    public Object[] args() {
        return args;
    }
}
