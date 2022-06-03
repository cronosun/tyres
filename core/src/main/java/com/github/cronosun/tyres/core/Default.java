package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * See {@link EntryInfo.TextEntry#defaultValue()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Default {
  String value();
}
