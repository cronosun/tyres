package com.github.cronosun.tyres.defaults.implementation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.defaults.backends.FallbackGenerator;
import com.github.cronosun.tyres.defaults.validation.Validator;
import java.util.Locale;
import java.util.Set;

final class DefaultCommon implements Resources.Common {

  private final Validator validator;
  private final FallbackGenerator fallbackGenerator;
  private final MsgNotFoundStrategy notFoundStrategy;

  DefaultCommon(
    Validator validator,
    FallbackGenerator fallbackGenerator,
    MsgNotFoundStrategy notFoundStrategy
  ) {
    this.validator = validator;
    this.fallbackGenerator = fallbackGenerator;
    this.notFoundStrategy = notFoundStrategy;
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
