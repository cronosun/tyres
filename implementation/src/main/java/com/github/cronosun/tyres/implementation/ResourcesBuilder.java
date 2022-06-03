package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ResourcesBuilder {

  private final ValueSupplier<DefaultNotFoundConfig> defaultNotFoundConfig = constant(
    DefaultNotFoundConfig.FALLBACK
  );
  private final ValueSupplier<MessageFormatter> messageFormatBackend = supplier(
    MessageFormatter::newCachedMessageFormatter
  );
  private final ValueSupplier<TextBackend> textBackend = supplier(() ->
    new ResourceBundleTextBackend(messageFormatBackend().getGet(), null)
  );
  private final ValueSupplier<BinBackend> binBackend = constant(
    BinBackend.resourceBundleInstance()
  );
  private final ValueSupplier<FallbackGenerator> fallbackGenerator = constant(
    FallbackGenerator.defaultInstance()
  );
  private final ValueSupplier<CurrentLocaleProvider> currentLocaleProvider = constant(
    CurrentLocaleProvider.nullProvider()
  );
  private final ValueSupplier<ArgsResolver> argsResolver = constant(
    ArgsResolver.defaultInstance()
  );
  private final ValueSupplier<EffectiveNameGenerator> effectiveNameGenerator = constant(
    EffectiveNameGenerator.empty()
  );
  private final ValueSupplier<Boolean> validateOnBundleUse = constant(false);
  private boolean built = false;

  /**
   * Builds {@link Resources}. After calling this method, the builder cannot be re-used.
   */
  public Resources build() {
    if (built) {
      throw new IllegalStateException("Cannot re-use the builder.");
    }
    built = true;
    return resources().getGet();
  }

  private final ValueSupplier<ResourcesBackend> resourcesBackend = supplier(() ->
    new DefaultResourcesBackend(
      textBackend().getGet(),
      binBackend().getGet(),
      argsResolver().getGet(),
      fallbackGenerator().getGet(),
      validator().getGet(),
      effectiveNameGenerator().get()
    )
  );

  public ValueSupplier<DefaultNotFoundConfig> defaultNotFoundConfig() {
    return defaultNotFoundConfig;
  }

  public ValueSupplier<MessageFormatter> messageFormatBackend() {
    return messageFormatBackend;
  }

  public ValueSupplier<TextBackend> textBackend() {
    return textBackend;
  }

  private final ValueSupplier<BundleFactory> bundleFactory = supplier(() ->
    new DefaultBundleFactory(
      resources().get(),
      resourcesBackend().getGet(),
      effectiveNameGenerator().get()
    )
  );

  public ValueSupplier<BinBackend> binBackend() {
    return binBackend;
  }

  public ValueSupplier<FallbackGenerator> fallbackGenerator() {
    return fallbackGenerator;
  }

  private final ValueSupplier<BundleCache> bundleCache = supplier(() ->
    BundleCache.newDefault(bundleFactory().getGet())
  );

  public ValueSupplier<CurrentLocaleProvider> currentLocaleProvider() {
    return currentLocaleProvider;
  }

  public ValueSupplier<ArgsResolver> argsResolver() {
    return argsResolver;
  }

  private final ValueSupplier<ValidatorBackend> validatorForCache = supplier(() ->
    new DefaultValidator(resources().get(), bundleFactory().getGet(), resourcesBackend().getGet())
  );

  public ValueSupplier<ResourcesBackend> resourcesBackend() {
    return resourcesBackend;
  }

  public ValueSupplier<EffectiveNameGenerator> effectiveNameGenerator() {
    return effectiveNameGenerator;
  }

  public ValueSupplier<Resources> resources() {
    return resources;
  }

  private final ValueSupplier<ValidatorBackend> validator = supplier(() ->
    new CachedConfigurableValidator(validatorForCache().get(), validateOnBundleUse().getGet())
  );

  public ValueSupplier<BundleFactory> bundleFactory() {
    return bundleFactory;
  }

  public ValueSupplier<BundleCache> bundleCache() {
    return bundleCache;
  }

  private final ValueSupplier<Resources> resources = supplier(() ->
    new DefaultResources(
      defaultNotFoundConfig().getGet(),
      bundleCache().getGet(),
      currentLocaleProvider().getGet(),
      validator().get()
    )
  );

  public ValueSupplier<ValidatorBackend> validator() {
    return validator;
  }

  public ValueSupplier<ValidatorBackend> validatorForCache() {
    return validatorForCache;
  }

  public ValueSupplier<Boolean> validateOnBundleUse() {
    return validateOnBundleUse;
  }

  public ResourcesBuilder defaultNotFoundConfig(DefaultNotFoundConfig defaultNotFoundConfig) {
    this.defaultNotFoundConfig.setValue(defaultNotFoundConfig);
    return this;
  }

  public ResourcesBuilder throwIfNotFound() {
    return defaultNotFoundConfig(DefaultNotFoundConfig.THROW);
  }

  public ResourcesBuilder currentLocaleProvider(CurrentLocaleProvider currentLocaleProvider) {
    this.currentLocaleProvider().setValue(currentLocaleProvider);
    return this;
  }

  public ResourcesBuilder effectiveNameGenerator(EffectiveNameGenerator effectiveNameGenerator) {
    this.effectiveNameGenerator().setValue(effectiveNameGenerator);
    return this;
  }

  public ResourcesBuilder validateOnBundleUse(boolean validateOnBundleUse) {
    validateOnBundleUse().setValue(validateOnBundleUse);
    return this;
  }

  private <T> ValueSupplier<T> constant(T defaultValue) {
    return new ValueSupplier<>(() -> Once.fromValue(defaultValue));
  }

  private <T> ValueSupplier<T> supplier(Supplier<T> supplier) {
    return new ValueSupplier<>(() -> Once.fromSupplier(supplier));
  }

  public final class ValueSupplier<T> implements Supplier<Once<T>> {

    private final Supplier<Once<T>> defaultOnce;

    @Nullable
    private Function<ResourcesBuilder, Once<T>> customOnce;

    @Nullable
    private Once<T> value;

    private ValueSupplier(Supplier<Once<T>> defaultOnce) {
      this.defaultOnce = defaultOnce;
    }

    public void setValue(T value) {
      assertNotYetComputed();
      this.customOnce = _ignored -> Once.fromValue(value);
    }

    public void setSupplier(Function<ResourcesBuilder, T> supplier) {
      assertNotYetComputed();
      this.customOnce = implementation -> Once.fromSupplier(() -> supplier.apply(implementation));
    }

    private void assertNotYetComputed() {
      if (this.value != null) {
        throw new IllegalStateException(
          "Cannot set the value, the value has already been constructed. " +
          "Do not re-use builders."
        );
      }
    }

    private Once<T> computeValue() {
      var customOnce = this.customOnce;
      if (customOnce != null) {
        return customOnce.apply(ResourcesBuilder.this);
      } else {
        return defaultOnce.get();
      }
    }

    @Override
    public Once<T> get() {
      var value = this.value;
      if (value == null) {
        value = computeValue();
        this.value = value;
      }
      return value;
    }

    public T getGet() {
      return get().get();
    }
  }
}
