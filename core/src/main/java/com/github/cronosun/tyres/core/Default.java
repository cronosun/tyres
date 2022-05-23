package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * See {@link ResInfo.Str#defaultValue()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Default {
  String value();
}
