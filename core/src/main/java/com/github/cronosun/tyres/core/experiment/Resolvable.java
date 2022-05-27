package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.function.Function;

public interface Resolvable extends WithConciseDebugString {
  Text resolve(Resources2 resources);

  /**
   * Returns a {@link Resolvable} from the given bundle and function. Note: The function must be constant,
   * as the implementation is allowed to cache the return value of the function.
   */
  static <T> Resolvable constant(Class<T> bundleClass, Function<T, Text> function) {
    return ResolvableConst.of(bundleClass, function);
  }
}
