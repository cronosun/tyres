package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import com.github.cronosun.tyres.core.experiment.Resources2;
import java.util.stream.Stream;

public interface BundleFactory {
  <T> T createBundle(Resources2 resources, Class<T> bundleClass);

  /**
   * Returns all resources declared by this bundle.
   *
   * Note 1: The bundle must be a bundle created by {@link #createBundle(Resources2, Class)} - method
   * is allowed to throw otherwiese.
   * Note 2: This is an optional operation. The implementation is allowed to return an empty stream
   * if this operation is not supported.
   */
  Stream<ResInfo> declaredResoucesForValidation(Object bundle);
}
