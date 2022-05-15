package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Marks an entire class or a single method as thread-safe (for documentation).
 */
@Retention(RetentionPolicy.CLASS)
@Documented
@Target(value = { ElementType.METHOD, ElementType.TYPE })
@Inherited
public @interface ThreadSafe {
}
