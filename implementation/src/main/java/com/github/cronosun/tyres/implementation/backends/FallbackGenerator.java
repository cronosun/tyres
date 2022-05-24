package com.github.cronosun.tyres.implementation.backends;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;

@ThreadSafe
public interface FallbackGenerator {
  /**
   * Returns the default implementation.
   */
  static FallbackGenerator defaultImplementation() {
    return DefaultFallbackGenerator.instance();
  }

  String generateFallbackMessageFor(ResInfo resInfo, Object[] args);
}
