package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public interface BundleInfo {
    /**
     * Returns the bundle class (the one that has been used to create the instance,
     * see {@link TyResImplementation#createInstance(Class)}).
     * <p>
     * Note: This is not neccesarily the same as the class from method ({@link Method#getDeclaringClass()},
     * see {@link ResInfo#method()}), since the method might have been declared on one of the inherited interfaces.
     */
    Class<?> bundleClass();

    /**
     * It's either `null` or contains the value from {@link Package} if the bundle class has been
     * annotated using this that annotation.
     */
    @Nullable
    List<String> customPackage();
}
