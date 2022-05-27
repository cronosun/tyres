package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class ImplementationBuilder {

  public Resources build() {
    var resources = resources().get().get();
    reset();
    return resources;
  }

  public void reset() {
    defaultNotFoundConfig().reset();
    messageFormatBackend().reset();
    textBackend().reset();
    binBackend().reset();
    fallbackGenerator().reset();
    currentLocaleProvider().reset();
    argsResolver().reset();
    resourcesBackend().reset();
    effectiveNameGenerator().reset();
    bundleCache().reset();
    bundleCache().reset();
    validator().reset();
    validatorForCache().reset();
    validateOnBundleUse().reset();
    resources().reset();
  }

  private final ValueSupplier<DefaultNotFoundConfig> defaultNotFoundConfig = constant(
    DefaultNotFoundConfig.FALLBACK
  );
  private final ValueSupplier<MessageFormatBackend> messageFormatBackend = constant(
    MessageFormatBackend.defaultInstance()
  );
  private final ValueSupplier<TextBackend> textBackend = supplier(() -> {
    var messageFormatBackend = messageFormatBackend().get();
    return new ResourceBundleTextBackend(messageFormatBackend.get(), null);
  });
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
  private final ValueSupplier<ResourcesBackend> resourcesBackend = supplier(() ->
    new DefaultResourcesBackend(
      textBackend().get().get(),
      binBackend().get().get(),
      argsResolver().get().get(),
      fallbackGenerator().get().get(),
      validator().get().get()
    )
  );
  private final ValueSupplier<EffectiveNameGenerator> effectiveNameGenerator = constant(
    EffectiveNameGenerator.empty()
  );
  private final ValueSupplier<BundleFactory> bundleFactory = supplier(() ->
    new DefaultBundleFactory(
      resources().get(),
      resourcesBackend().get().get(),
      effectiveNameGenerator().get().get()
    )
  );
  private final ValueSupplier<BundleCache> bundleCache = supplier(() ->
    BundleCache.newDefault(bundleFactory().get().get())
  );
  private final ValueSupplier<ValidatorBackend> validatorForCache = supplier(() ->
    new DefaultValidator(
      resources().get(),
      bundleFactory().get().get(),
      resourcesBackend().get().get()
    )
  );
  private final ValueSupplier<Boolean> validateOnBundleUse = constant(false);
  private final ValueSupplier<ValidatorBackend> validator = supplier(() ->
    new CachedConfigurableValidator(validatorForCache().get(), validateOnBundleUse().get().get())
  );

  private final ValueSupplier<Resources> resources = supplier(() ->
    new DefaultResources(
      defaultNotFoundConfig().get().get(),
      bundleCache().get().get(),
      currentLocaleProvider().get().get(),
      validator().get()
    )
  );

  public ValueSupplier<DefaultNotFoundConfig> defaultNotFoundConfig() {
    return defaultNotFoundConfig;
  }

  public ValueSupplier<MessageFormatBackend> messageFormatBackend() {
    return messageFormatBackend;
  }

  public ValueSupplier<TextBackend> textBackend() {
    return textBackend;
  }

  public ValueSupplier<BinBackend> binBackend() {
    return binBackend;
  }

  public ValueSupplier<FallbackGenerator> fallbackGenerator() {
    return fallbackGenerator;
  }

  public ValueSupplier<CurrentLocaleProvider> currentLocaleProvider() {
    return currentLocaleProvider;
  }

  public ValueSupplier<ArgsResolver> argsResolver() {
    return argsResolver;
  }

  public ValueSupplier<ResourcesBackend> resourcesBackend() {
    return resourcesBackend;
  }

  public ValueSupplier<EffectiveNameGenerator> effectiveNameGenerator() {
    return effectiveNameGenerator;
  }

  public ValueSupplier<Resources> resources() {
    return resources;
  }

  public ValueSupplier<BundleFactory> bundleFactory() {
    return bundleFactory;
  }

  public ValueSupplier<BundleCache> bundleCache() {
    return bundleCache;
  }

  public ValueSupplier<ValidatorBackend> validator() {
    return validator;
  }

  public ValueSupplier<ValidatorBackend> validatorForCache() {
    return validatorForCache;
  }

  public ValueSupplier<Boolean> validateOnBundleUse() {
    return validateOnBundleUse;
  }

  public ImplementationBuilder defaultNotFoundConfig(DefaultNotFoundConfig defaultNotFoundConfig) {
    this.defaultNotFoundConfig.setValue(defaultNotFoundConfig);
    return this;
  }

  public ImplementationBuilder throwIfNotFound() {
    return defaultNotFoundConfig(DefaultNotFoundConfig.THROW);
  }

  public ImplementationBuilder currentLocaleProvider(CurrentLocaleProvider currentLocaleProvider) {
    this.currentLocaleProvider().setValue(currentLocaleProvider);
    return this;
  }

  public ImplementationBuilder effectiveNameGenerator(
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    this.effectiveNameGenerator().setValue(effectiveNameGenerator);
    return this;
  }

  public ImplementationBuilder validateOnBundleUse(boolean validateOnBundleUse) {
    validateOnBundleUse().setValue(validateOnBundleUse);
    return this;
  }

  private <T> ValueSupplier<T> constant(T defaultValue) {
    return new ValueSupplier<>(() -> Once.fromValue(defaultValue));
  }

  private <T> ValueSupplier<T> supplier(Supplier<T> defaultSupplier) {
    return new ValueSupplier<>(() -> Once.fromSupplier(defaultSupplier));
  }

  public final class ValueSupplier<T> implements Supplier<Once<T>> {

    private final Supplier<Once<T>> defaultOnce;

    @Nullable
    private Function<ImplementationBuilder, Once<T>> customOnce;

    @Nullable
    private Once<T> value;

    private ValueSupplier(Supplier<Once<T>> defaultOnce) {
      this.defaultOnce = defaultOnce;
    }

    private void reset() {
      customOnce = null;
      this.value = null;
    }

    public void setValue(T value) {
      assertNotYetComputed();
      this.customOnce = _ignored -> Once.fromValue(value);
    }

    public void setSupplier(Function<ImplementationBuilder, T> supplier) {
      assertNotYetComputed();
      this.customOnce = implementation -> Once.fromSupplier(() -> supplier.apply(implementation));
    }

    private void assertNotYetComputed() {
      if (this.value != null) {
        throw new IllegalStateException(
          "Cannot set the value, the value has already been constructed. " +
          "Reset this builder first."
        );
      }
    }

    private Once<T> computeValue() {
      var current = this.customOnce;
      if (current != null) {
        return current.apply(ImplementationBuilder.this);
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
  }
}
