package com.github.cronosun.tyres.core.experiment;

import java.util.function.Function;

public interface Resolvable {
  Text get(Resources2 resources);

  /**
   * Returns a {@link Resolvable} from the given bundle and function. Note: The function must be constant,
   * the implementation is allowed to cache the return value of the function.
   */
  static <T> Resolvable constant(Class<T> bundleClass, Function<T, Text> function) {
    return ResolvableConst.of(bundleClass, function);
  }
}
