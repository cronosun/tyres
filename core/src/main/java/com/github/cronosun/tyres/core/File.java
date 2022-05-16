package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Marks a method as a file-resouce.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface File {
  String filename();
}
