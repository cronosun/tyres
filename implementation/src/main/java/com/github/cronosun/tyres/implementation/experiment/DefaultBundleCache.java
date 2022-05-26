package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.experiment.Resources2;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class DefaultBundleCache implements BundleCache {

  private volatile CacheEntry<?> lastEntry;
  private final Map<Class<?>, CacheEntry<?>> cache = new ConcurrentHashMap<>();
  private final Object lock = new Object();

  @Override
  public <T> T bundle(Resources2 resources, Class<T> bundleClass, BundleFactory factory) {
    // fast cache: last bundle (will usually be the correct one), compare identity only.
    var lastEntry = this.lastEntry;
    if (lastEntry != null && lastEntry.bundleClass == bundleClass) {
      //noinspection unchecked
      return (T) lastEntry.bundle;
    }

    var cacheEntry = bundleNoFastCache(resources, bundleClass, factory);
    this.lastEntry = cacheEntry;
    return cacheEntry.bundle;
  }

  private <T> CacheEntry<T> bundleNoFastCache(
    Resources2 resources,
    Class<T> bundleClass,
    BundleFactory factory
  ) {
    var fromCache = this.cache.get(bundleClass);
    if (fromCache == null) {
      synchronized (lock) {
        fromCache = this.cache.get(bundleClass);
        if (fromCache == null) {
          // ok, still not here, ask the factory
          var bundle = factory.createBundle(resources, bundleClass);
          var cacheEntry = new CacheEntry<>(bundleClass, bundle);
          this.cache.put(bundleClass, cacheEntry);
          return cacheEntry;
        } else {
          //noinspection unchecked
          return (CacheEntry<T>) fromCache;
        }
      }
    } else {
      //noinspection unchecked
      return (CacheEntry<T>) fromCache;
    }
  }

  private static final class CacheEntry<T> {

    private final Class<T> bundleClass;
    private final T bundle;

    private CacheEntry(Class<T> bundleClass, T bundle) {
      this.bundleClass = bundleClass;
      this.bundle = bundle;
    }
  }
}
