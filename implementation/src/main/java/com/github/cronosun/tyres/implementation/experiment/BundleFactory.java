package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.Resources2;

public interface BundleFactory {
  <T> T createBundle(Resources2 resources, Class<T> bundleClass);
}
