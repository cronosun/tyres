package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;

@ThreadSafe
public interface BundleInfo {
  /**
   * Returns the bundle class (the one that has been used to create the instance,
   * see {@link TyResImplementation#createInstance(Class)}).
   * <p>
   * Note: This is not necessarily the same as the class from method ({@link Method#getDeclaringClass()},
   * see {@link ResInfo#method()}), since the method might have been declared on one of the
   * inherited interfaces.
   */
  Class<?> bundleClass();

  /**
   * Returns the base name.
   * <p>
   * See {@link BaseName}, {@link RenameName}, {@link RenamePackage}. If the names are not renamed, the base name
   * is taken from {@link #bundleClass()}: {@link BaseName#fromClass(Class)} is called with {@link #bundleClass()}.
   */
  BaseName baseName();

  /**
   * Returns the implementation that has been used to create this.
   */
  TyResImplementation implementation();

  /**
   * Returns information about this instance for debugging.
   */
  String toString();
}
