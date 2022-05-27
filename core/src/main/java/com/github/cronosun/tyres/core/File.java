package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Marks a method as a file-resource.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface File {
  /**
   * The filename, such as "my_data.json" or "my_image.png".
   */
  String value();
}
