package com.github.cronosun.tyres.core;

@ThreadSafe
public interface TyResImplementation {
  <T> T createInstance(Class<T> bundleClass);

  /**
   * Returns the bundle resource info for given object. Contract: The given instance
   * must be an instance that has been returned by this implementation (see
   * {@link #createInstance(Class)}) - errors otherwise.
   * <p>
   * Performance: Implementations are encouraged to store the bundle res info and return
   * a stored instance here rather than creating a new instance.
   */
  ReflectionInfo bundleResInfo(Object instance);
}
