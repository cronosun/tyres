package com.github.cronosun.tyres.spring;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FactoryForBundle {
  Class<?> value();
}
