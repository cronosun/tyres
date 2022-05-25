package com.github.cronosun.tyres.core.experiment;

import java.util.function.Function;

public interface Resolvable {
    Text get(NewResouces resouces);

    static <T> Resolvable of(Class<T> bundleClass, Function<T, Text> function) {
        return ResolvableImpl.of(bundleClass, function);
    }
}
