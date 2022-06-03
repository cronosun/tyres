package com.github.cronosun.tyres.implementation;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class NullCurrentLocaleProvider implements CurrentLocaleProvider {

  private static final NullCurrentLocaleProvider INSTANCE = new NullCurrentLocaleProvider();

  private NullCurrentLocaleProvider() {}

  public static NullCurrentLocaleProvider instance() {
    return INSTANCE;
  }

  @Override
  public @Nullable Locale currentLocale() {
    return null;
  }
}
