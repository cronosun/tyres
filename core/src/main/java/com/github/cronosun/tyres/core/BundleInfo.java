package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Objects;

@ThreadSafe
public final class BundleInfo {
  private final Class<?> bundleClass;
  private final BaseName baseName;
  private final TyResImplementation implementation;

  public BundleInfo(Class<?> bundleClass, BaseName baseName, TyResImplementation implementation) {
    this.bundleClass = Objects.requireNonNull(bundleClass);
    this.baseName = Objects.requireNonNull(baseName);
    this.implementation = Objects.requireNonNull(implementation);
  }

  /**
   * Returns the bundle class (the one that has been used to create the instance,
   * see {@link TyResImplementation#createInstance(Class)}).
   * <p>
   * Note: This is not necessarily the same as the class from method ({@link Method#getDeclaringClass()},
   * see {@link ResInfo#method()}), since the method might have been declared on one of the
   * inherited interfaces.
   */
  public Class<?> bundleClass() {
    return bundleClass;
  }

  /**
   * Returns the base name.
   * <p>
   * See {@link BaseName}, {@link RenameName}, {@link RenamePackage}. If the names are not renamed, the base name
   * is taken from {@link #bundleClass()}: {@link BaseName#fromClass(Class)} is called with {@link #bundleClass()}.
   */
  public BaseName baseName() {
    return baseName;
  }

  /**
   * Returns the implementation that has been used to create this.
   */
  public TyResImplementation implementation() {
    return implementation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BundleInfo that = (BundleInfo) o;
    return bundleClass.equals(that.bundleClass) && baseName.equals(that.baseName) && implementation.equals(that.implementation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleClass, baseName, implementation);
  }

  @Override
  public String toString() {
    return "BundleInfo{" +
            "bundleClass=" + bundleClass +
            ", baseName=" + baseName +
            ", implementation=" + implementation +
            '}';
  }
}
