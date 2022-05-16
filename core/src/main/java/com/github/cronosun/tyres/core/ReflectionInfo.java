package com.github.cronosun.tyres.core;

import java.util.Collection;

/**
 * Information about all resources in the bundle and information about the bundle
 * itself.
 */
@ThreadSafe
public interface ReflectionInfo {
  /**
   * Default reflection implementation that conforms to the specification and can be used by
   * implementations (unless they want to provide their own implementation).
   */
  static ReflectionInfo getFrom(Class<?> bundleClass, TyResImplementation implementation) {
    return Reflection.reflect(implementation, bundleClass);
  }

  BundleInfo bundleInfo();

  /**
   * Returns all declared resources.
   *
   * Expect the collection to be immutable.
   */
  Collection<ResInfo> resources();
}
