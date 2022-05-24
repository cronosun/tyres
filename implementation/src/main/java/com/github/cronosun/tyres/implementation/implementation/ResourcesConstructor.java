package com.github.cronosun.tyres.implementation.implementation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.implementation.backends.BinBackend;
import com.github.cronosun.tyres.implementation.backends.FallbackGenerator;
import com.github.cronosun.tyres.implementation.backends.MsgStrBackend;
import com.github.cronosun.tyres.implementation.validation.Validator;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class ResourcesConstructor {

  private final MsgNotFoundStrategy notFoundStrategy;

  @Nullable
  private FallbackGenerator fallbackGenerator;

  @Nullable
  private MsgStrBackend msgStrBackend;

  @Nullable
  private BinBackend binBackend;

  @Nullable
  private Validator validator;

  public ResourcesConstructor(MsgNotFoundStrategy notFoundStrategy) {
    this.notFoundStrategy = notFoundStrategy;
  }

  public Resources construct() {
    var presentStringBackend = Objects.requireNonNullElse(
      msgStrBackend,
      MsgStrBackend.backendUsingResourceBundle()
    );
    var presentFallbackGenerator = Objects.requireNonNullElse(
      fallbackGenerator,
      FallbackGenerator.defaultImplementation()
    );
    var presentNotFoundStrategy = Objects.requireNonNull(notFoundStrategy);
    var presentBindBackend = Objects.requireNonNullElse(
      binBackend,
      BinBackend.backendUsingResources()
    );
    var presentValidator = Objects.requireNonNullElseGet(
      validator,
      () -> Validator.newDefaultValidator(presentStringBackend, presentBindBackend)
    );
    return new DefaultResources(
      presentNotFoundStrategy,
      presentFallbackGenerator,
      presentStringBackend,
      presentBindBackend,
      presentValidator
    );
  }

  public ResourcesConstructor fallbackGenerator(FallbackGenerator fallbackGenerator) {
    this.fallbackGenerator = fallbackGenerator;
    return this;
  }

  public ResourcesConstructor msgStrBackend(MsgStrBackend msgStrBackend) {
    this.msgStrBackend = msgStrBackend;
    return this;
  }

  public ResourcesConstructor binBackend(BinBackend binBackend) {
    this.binBackend = binBackend;
    return this;
  }

  public ResourcesConstructor validator(Validator validator) {
    this.validator = validator;
    return this;
  }
}
