package com.github.cronosun.tyres.spring;

public interface BundleCreator {
  <T> T createBundle(Class<T> bundleClass);
}
