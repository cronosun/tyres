package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class DefaultResources implements Resources {

  private final Binaries bin;
  private final Strings str;
  private final Messages msg;
  private final Resolver resolver;
  private final Common common;

  // TODO: Make a config builder for that...
  public DefaultResources(
    MsgNotFoundStrategy notFoundStrategy,
    @Nullable FallbackGenerator fallbackGenerator,
    @Nullable StrBackend strBackend,
    @Nullable BinBackend binBackend,
    @Nullable Resolver resolver,
    @Nullable Validator validator
  ) {
    var presentStringBackend = Objects.requireNonNullElse(
      strBackend,
      StrBackend.usingResourceBundle()
    );
    var presentFallbackGenerator = Objects.requireNonNullElse(
      fallbackGenerator,
      FallbackGenerator.defaultImplementation()
    );
    var presentNotFoundStrategy = Objects.requireNonNull(notFoundStrategy);
    var presentBindBackend = Objects.requireNonNullElse(binBackend, BinBackend.usingResources());
    var presentResolver = Objects.requireNonNullElseGet(resolver, () -> new DefaultResolver(this));
    var presentValidator = Objects.requireNonNullElseGet(
      validator,
      () -> Validator.newDefaultImplementation(presentStringBackend, presentBindBackend)
    );

    this.common =
      new DefaultCommon(presentValidator, presentFallbackGenerator, presentNotFoundStrategy);
    this.str = new DefaultStrings(this, presentStringBackend);
    this.msg = new DefaultMsgResources(presentStringBackend, this);
    this.bin = new DefaultBinaries(presentBindBackend);
    this.resolver = presentResolver;
  }

  public static DefaultResources newDefault(MsgNotFoundStrategy notFoundStrategy) {
    return new DefaultResources(notFoundStrategy, null, null, null, null, null);
  }

  @Override
  public Messages msg() {
    return msg;
  }

  @Override
  public Strings str() {
    return str;
  }

  @Override
  public Binaries bin() {
    return bin;
  }

  @Override
  public Resolver resolver() {
    return resolver;
  }

  @Override
  public Common common() {
    return this.common;
  }
}
