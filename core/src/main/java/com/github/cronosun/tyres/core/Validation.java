package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Validation {
  /**
   * If this is true, the resource is considered to be optional and won't produce a "resource not found" validation
   * error. If the annotation is missing entirely, the default value is <code>false</code>.
   */
  boolean optional() default false;
}
