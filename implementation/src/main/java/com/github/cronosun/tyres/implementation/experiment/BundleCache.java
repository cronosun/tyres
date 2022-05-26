package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.Resources2;

public interface BundleCache {
  <T> T bundle(Resources2 resources, Class<T> bundleClass, BundleFactory factory);

  static BundleCache newDefault() {
    return new DefaultBundleCache();
  }
}
