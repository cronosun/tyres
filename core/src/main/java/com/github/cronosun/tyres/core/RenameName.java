package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Renames the name, see also {@link BaseName#name()}}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface RenameName {
  String value();
}
