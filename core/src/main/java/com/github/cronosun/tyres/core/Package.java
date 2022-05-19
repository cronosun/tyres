package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface Package {
  String[] value();
}
