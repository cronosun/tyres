package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public interface ResInfo {
    /**
     * Information about the bundle.
     */
    BundleInfo bundle();

    Method method();

    /**
     * The name. This is either {@link Method#getName()} or the name from the {@link Name}-annotation (if
     * this annotation is present).
     */
    String name();

    /**
     * The default value from the {@link Default}-annotation or null if there's no such annotation.
     */
    @Nullable
    String defaultValue();
}
