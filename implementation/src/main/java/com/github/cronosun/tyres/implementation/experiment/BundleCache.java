package com.github.cronosun.tyres.implementation.experiment;

public interface BundleCache {
  <T> T bundle(Class<T> bundleClass, BundleFactory factory);
}
