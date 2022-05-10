package com.github.cronosun.tyres;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseImplementation {
    /**
     * Must be a class that implements a static method called `instance()` that returns
     * {@link TyResImplementation}.
     */
    Class<?> value();
}
