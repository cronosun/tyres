package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * See {@link ResInfoDetails.StrResource#defaultValue()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Default {
  String value();
}
