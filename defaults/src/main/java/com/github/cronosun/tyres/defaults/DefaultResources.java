package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources implements Resources {

  private final BinResources bin;
  private final StrResources str;
  private final MsgResources msg;
  private final Validator validator;

  public DefaultResources(
    MsgNotFoundStrategy notFoundStrategy,
    @Nullable FallbackGenerator fallbackGenerator,
    @Nullable StrBackend strBackend,
    @Nullable BinBackend binBackend,
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

    this.str = new DefaultStrResources(presentStringBackend);
    this.msg =
      new DefaultMsgResources(
        presentStringBackend,
        presentNotFoundStrategy,
        presentFallbackGenerator
      );
    this.bin = new DefaultBinResources(presentBindBackend);
    this.validator =
      Objects.requireNonNullElseGet(
        validator,
        () -> Validator.newDefaultImplementation(presentStringBackend, presentBindBackend)
      );
  }

  public static DefaultResources newDefault(MsgNotFoundStrategy notFoundStrategy) {
    return new DefaultResources(notFoundStrategy, null, null, null, null);
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
  public void validate(Object bundle, Set<Locale> locales) {
    validator.validate(bundle, locales);
  }
}
