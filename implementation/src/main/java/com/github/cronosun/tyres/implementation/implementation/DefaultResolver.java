package com.github.cronosun.tyres.implementation.implementation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class DefaultResolver implements Resources.Resolver {

  private final Resources resources;

  DefaultResolver(Resources resources) {
    this.resources = resources;
  }

  @Override
  public @Nullable String maybe(Resolvable message, Locale locale) {
    return message.maybe(resources, locale);
  }

  @Override
  public String get(Resolvable message, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return message.get(resources, notFoundStrategy, locale);
  }

  @Override
  public String get(Resolvable message, Locale locale) {
    return get(message, resources.common().notFoundStrategy(), locale);
  }
}
