package com.github.cronosun.tyres.implementation.experiment;

public interface BundleFactory {
  <T> T createBundle(Class<T> bundleClass);
}
