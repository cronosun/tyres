package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.Resources;
import java.util.stream.Stream;

public interface BundleFactory {
  <T> T createBundle(Class<T> bundleClass);

  /**
   * Returns all resources declared by this bundle.
   * <p>
   * Note 1: The bundle must be a bundle created by {@link #createBundle(Class)} - method
   * is allowed to throw otherwise.
   * Note 2: This is an optional operation. The implementation is allowed to return an empty stream
   * if this operation is not supported.
   * Note 3: Only used this for validation since this operation (depending on the implementation) might be slow.
   */
  Stream<ResInfo> declaredResourcesForValidation(Object bundle);
}
