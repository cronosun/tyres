package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

final class DefaultResources implements Resources {

  private final DefaultNotFoundConfig defaultNotFoundConfig;
  private final BundleCache bundleCache;
  private final CurrentLocaleProvider currentLocaleProvider;
  private final Once<ValidatorBackend> validatorBackend;

  public DefaultResources(
    DefaultNotFoundConfig defaultNotFoundConfig,
    BundleCache bundleCache,
    CurrentLocaleProvider currentLocaleProvider,
    Once<ValidatorBackend> validatorBackend
  ) {
    this.defaultNotFoundConfig = Objects.requireNonNull(defaultNotFoundConfig);
    this.bundleCache = Objects.requireNonNull(bundleCache);
    this.currentLocaleProvider = Objects.requireNonNull(currentLocaleProvider);
    this.validatorBackend = validatorBackend;
  }

  @Override
  public <T> T get(Class<T> bundleClass) {
    return bundleCache.bundle(bundleClass);
  }

  @Override
  public @Nullable Locale currentLocale() {
    return currentLocaleProvider.currentLocale();
  }

  @Override
  public DefaultNotFoundConfig defaultNotFoundConfig() {
    return defaultNotFoundConfig;
  }

  @Override
  public void validate(Class<?> bundleClass, Locale locale) {
    this.validatorBackend.get().validate(ValidatorBackend.When.OTHER, bundleClass, locale);
  }
}
