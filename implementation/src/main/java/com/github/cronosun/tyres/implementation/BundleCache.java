package com.github.cronosun.tyres.implementation;

public interface BundleCache {
  /**
   * Returns a new instance of the default bundle cache.
   * <p>
   * Note: The default bundle cache NEVER evicts bundles from the cache. This is OK for most cases but can be
   * the wrong cache for applications that dynamically load and unload modules.
   */
  static BundleCache newDefault(BundleFactory bundleFactory) {
    return new DefaultBundleCache(bundleFactory);
  }

  <T> T bundle(Class<T> bundleClass);
}
