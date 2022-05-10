package com.github.cronosun.tyres;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

public interface ResInfo {
    /**
     * Returns the bundle class (the one that has been used to create the instance,
     * see {@link TyResImplementation#createInstance(Class)}).
     *
     * Note: This is not neccesarily the same as the class from method ({@link Method#getDeclaringClass()}),
     * since the method might have been declared on one of the inherited interfaces.
     */
    Class<?> bundleClass();

    Method method();

    /**
     * The name. This is either {@link Method#getName()} or the name from the {@link Name}-annotation (if
     * this annotation is present).
     */
    String name();

    /**
     * It's either `null` or contains the value from {@link Package} if the bundle class has been
     * annotated using this that annotation.
     */
    @Nullable
    List<String> customPackage();
}
