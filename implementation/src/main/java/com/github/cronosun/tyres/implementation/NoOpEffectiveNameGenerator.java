package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.Filename;
import java.lang.reflect.Method;

final class NoOpEffectiveNameGenerator implements EffectiveNameGenerator {

  private static final NoOpEffectiveNameGenerator INSTANCE = new NoOpEffectiveNameGenerator();

  private NoOpEffectiveNameGenerator() {}

  public static NoOpEffectiveNameGenerator instance() {
    return INSTANCE;
  }

  @Override
  public String effectiveNameForText(BundleInfo bundleInfo, Method method, String name) {
    return name;
  }

  @Override
  public Filename effectiveNameForBin(BundleInfo bundleInfo, Method method, Filename filename) {
    return filename;
  }

  @Override
  public BaseName effectiveBaseName(Class<?> bundleClass, BaseName declaredBaseName) {
    return declaredBaseName;
  }
}
