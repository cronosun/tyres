package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class DefaultResources implements Resources {

  private final BinResources bin;
  private final StrResources str;
  private final MsgResources msg;
  private final Validator validator;
  private final FallbackGenerator fallbackGenerator;
  private final MsgNotFoundStrategy notFoundStrategy;
  private final Resolver resolver;

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

    this.notFoundStrategy = presentNotFoundStrategy;
    this.fallbackGenerator = presentFallbackGenerator;
    this.str = new DefaultStrResources(this, presentStringBackend);
    this.msg = new DefaultMsgResources(presentStringBackend, this);
    this.bin = new DefaultBinResources(presentBindBackend);
    this.resolver = presentResolver;
    this.validator =
      Objects.requireNonNullElseGet(
        validator,
        () -> Validator.newDefaultImplementation(presentStringBackend, presentBindBackend)
      );
  }

  public static DefaultResources newDefault(MsgNotFoundStrategy notFoundStrategy) {
    return new DefaultResources(notFoundStrategy, null, null, null, null, null);
  }

  @Override
  public MsgResources msg() {
    return msg;
  }

  @Override
  public StrResources str() {
    return str;
  }

  @Override
  public BinResources bin() {
    return bin;
  }

  @Override
  public Resolver resolver() {
    return resolver;
  }

  @Override
  public void validate(Object bundle, Set<Locale> locales) {
    validator.validate(bundle, locales);
  }

  @Override
  public MsgNotFoundStrategy notFoundStrategy() {
    return this.notFoundStrategy;
  }

  @Override
  public String fallbackFor(ResInfo resInfo, Object[] args) {
    return fallbackGenerator.generateFallbackMessageFor(resInfo, args);
  }
}
