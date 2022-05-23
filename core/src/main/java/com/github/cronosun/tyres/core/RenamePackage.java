package com.github.cronosun.tyres.core;

import java.lang.annotation.*;

/**
 * Renames the package name, see also {@link BaseName#packageName()}.
 * <p>
 * Note: DO NOT use this to change how bundles are located - e.g. if you want to have one properties-file
 * with all messages inside, do not set the package name of all bundles to a fixed name. If you want
 * to do this, change the {@link Resources} implementation. Most implementations (especially validation)
 * won't work correctly if you have multiple bundles with the same package name. Use this annotation
 * only if you want to change something and you - for some reason - can't move the properties-file.
 *
 * @see RenameName
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface RenamePackage {
  String value();
}
