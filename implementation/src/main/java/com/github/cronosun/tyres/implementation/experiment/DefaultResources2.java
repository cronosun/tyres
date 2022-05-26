package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.Resources2;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources2 implements Resources2 {

  public static DefaultResources2 todoNewInstance(DefaultNotFoundConfig defaultNotFoundConfig) {
    var messageFormatBackend = MessageFormatBackend.defaultInstance();
    var textBackend = new ResourceBundleTextBackend(messageFormatBackend, null);
    var binBackend = BinBackend.resourceBundleInstance();
    var fallbackGenerator = FallbackGenerator.defaultInstance();
    var currentLocaleProvider = CurrentLocaleProvider.nullProvider();
    var argsResolver = ArgsResolver.defaultInstance();
    var resourcesBackend = new DefaultResourcesBackend(
      textBackend,
      binBackend,
      argsResolver,
      fallbackGenerator
    );
    var effectiveNameGenerator = EffectiveNameGenerator.empty();
    return new DefaultResources2(
      defaultNotFoundConfig,
      BundleCache.newDefault(),
      new DefaultBundleFactory(resourcesBackend, effectiveNameGenerator),
      currentLocaleProvider
    );
  }

  private final DefaultNotFoundConfig defaultNotFoundConfig;
  private final BundleCache bundleCache;
  private final BundleFactory bundleFactory;
  private final CurrentLocaleProvider currentLocaleProvider;

  public DefaultResources2(
    DefaultNotFoundConfig defaultNotFoundConfig,
    BundleCache bundleCache,
    BundleFactory bundleFactory,
    CurrentLocaleProvider currentLocaleProvider
  ) {
    this.defaultNotFoundConfig = Objects.requireNonNull(defaultNotFoundConfig);
    this.bundleCache = Objects.requireNonNull(bundleCache);
    this.bundleFactory = Objects.requireNonNull(bundleFactory);
    this.currentLocaleProvider = Objects.requireNonNull(currentLocaleProvider);
  }

  @Override
  public <T> T get(Class<T> bundleClass) {
    return bundleCache.bundle(this, bundleClass, this.bundleFactory);
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
