package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.Resources2;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources2 implements Resources2 {

  private final DefaultNotFoundConfig defaultNotFoundConfig;
  private final BundleCache bundleCache;
  private final BundleFactory bundleFactory;
  private final CurrentLocaleProvider currentLocaleProvider;
  private final EffectiveNameGenerator effectiveNameGenerator;

  public DefaultResources2(
    DefaultNotFoundConfig defaultNotFoundConfig,
    BundleCache bundleCache,
    BundleFactory bundleFactory,
    CurrentLocaleProvider currentLocaleProvider,
    EffectiveNameGenerator effectiveNameGenerator) {
    this.defaultNotFoundConfig = defaultNotFoundConfig;
    this.bundleCache = bundleCache;
    this.bundleFactory = bundleFactory;
    this.currentLocaleProvider = currentLocaleProvider;
    this.effectiveNameGenerator = effectiveNameGenerator;
  }

  @Override
  public <T> T get(Class<T> bundleClass) {
    return bundleCache.bundle(bundleClass, this.bundleFactory, effectiveNameGenerator);
  }

  @Override
  public @Nullable Locale currentLocale() {
    return currentLocaleProvider.currentLocale();
  }

  @Override
  public DefaultNotFoundConfig defaultNotFoundConfig() {
    return defaultNotFoundConfig;
  }
}
