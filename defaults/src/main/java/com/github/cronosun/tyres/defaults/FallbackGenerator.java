package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;

/**
 * Implementations of this class must be thread-safe.
 */
@ThreadSafe
public interface FallbackGenerator {
  String generateFallbackMessageFor(ResInfo resInfo, Object[] args);

  /**
   * Returns the default implementation.
   */
  static FallbackGenerator defaultImplementation() {
    return DefaultFallbackGenerator.instance();
  }
}
