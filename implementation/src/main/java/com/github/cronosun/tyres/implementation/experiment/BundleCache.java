package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.Resources;

public interface BundleCache {
  <T> T bundle(Resources resources, Class<T> bundleClass, BundleFactory factory);

  static BundleCache newDefault() {
    return new DefaultBundleCache();
  }
}
