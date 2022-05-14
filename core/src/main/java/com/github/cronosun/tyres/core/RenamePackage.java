package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Renames the package name, see also {@link BaseName#packageName()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface RenamePackage {
    String value() default "";
}
