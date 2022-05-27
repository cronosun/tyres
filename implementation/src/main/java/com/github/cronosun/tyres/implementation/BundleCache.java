package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Resources;

public interface BundleCache {
  <T> T bundle(Resources resources, Class<T> bundleClass, BundleFactory factory);

  /**
   * Returns a new instance of the default bundle cache.
   * <p>
   * Note: The default bundle cache NEVER evicts bundles from the cache. This is OK for most cases but can be
   * the wrong cache for applications that dynamically load and unload modules.
   */
  static BundleCache newDefault() {
    return new DefaultBundleCache();
  }
}
