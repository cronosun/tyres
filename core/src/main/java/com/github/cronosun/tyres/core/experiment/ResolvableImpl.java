package com.github.cronosun.tyres.core.experiment;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

final class ResolvableImpl<T> implements Resolvable {

    @Nullable
    private volatile Text cachedText;

    private ResolvableImpl(Class<T> bundleClass, Function<T, Text> function) {
        this.bundleClass = bundleClass;
        this.function = function;
    }

    public static <T> ResolvableImpl<T> of(Class<T> bundleClass, Function<T, Text> function) {
        return new ResolvableImpl<>(bundleClass, function);
    }

    private final Class<T> bundleClass;
    private final Function<T, Text> function;

    @Override
    public Text get(NewResouces resouces) {
        var cachedText = this.cachedText;
        if (cachedText!=null) {
            return cachedText;
        }
        var bundle = resouces.get(this.bundleClass);
        cachedText = this.function.apply(bundle);
        this.cachedText = cachedText;
        return cachedText;
    }
}
