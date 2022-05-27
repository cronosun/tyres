package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources implements Resources {

  public static DefaultResources todoNewInstance(DefaultNotFoundConfig defaultNotFoundConfig) {
    return todoNewInstance(defaultNotFoundConfig, null);
  }

  public static DefaultResources todoNewInstance(
    DefaultNotFoundConfig defaultNotFoundConfig,
    @Nullable CurrentLocaleProvider givenCurrentLocaleProvider
  ) {
    var messageFormatBackend = MessageFormatBackend.defaultInstance();
    var textBackend = new ResourceBundleTextBackend(messageFormatBackend, null);
    var binBackend = BinBackend.resourceBundleInstance();
    var fallbackGenerator = FallbackGenerator.defaultInstance();
    var currentLocaleProvider = Objects.requireNonNullElse(
      givenCurrentLocaleProvider,
      CurrentLocaleProvider.nullProvider()
    );
    var argsResolver = ArgsResolver.defaultInstance();
    var resourcesBackend = new DefaultResourcesBackend(
      textBackend,
      binBackend,
      argsResolver,
      fallbackGenerator
    );
    var effectiveNameGenerator = EffectiveNameGenerator.empty();
    var bundleFactory = new DefaultBundleFactory(resourcesBackend, effectiveNameGenerator);
    var validatorBackend = new DefaultValidator(bundleFactory, resourcesBackend);
    return new DefaultResources(
      defaultNotFoundConfig,
      BundleCache.newDefault(),
      bundleFactory,
      currentLocaleProvider,
      validatorBackend
    );
  }

  private final DefaultNotFoundConfig defaultNotFoundConfig;
  private final BundleCache bundleCache;
  private final BundleFactory bundleFactory;
  private final CurrentLocaleProvider currentLocaleProvider;
  private final ValidatorBackend validatorBackend;

  public DefaultResources(
    DefaultNotFoundConfig defaultNotFoundConfig,
    BundleCache bundleCache,
    BundleFactory bundleFactory,
    CurrentLocaleProvider currentLocaleProvider,
    ValidatorBackend validatorBackend
  ) {
    this.defaultNotFoundConfig = Objects.requireNonNull(defaultNotFoundConfig);
    this.bundleCache = Objects.requireNonNull(bundleCache);
    this.bundleFactory = Objects.requireNonNull(bundleFactory);
    this.currentLocaleProvider = Objects.requireNonNull(currentLocaleProvider);
    this.validatorBackend = validatorBackend;
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

  @Override
  public void validate(Class<?> bundleClass, Locale locale) {
    this.validatorBackend.validateManually(this, bundleClass, locale);
  }
}
