package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;

/**
 * Implementations of this class must be tread-safe.
 */
@ThreadSafe
public interface ResourceBundleProvider {
  /**
   * Returns the bundle. Never returns <code>null</code>: If there's no such bundle, returns an empty bundle.
   */
  TyResResourceBundle getBundleFor(ResInfo resInfo);
}
