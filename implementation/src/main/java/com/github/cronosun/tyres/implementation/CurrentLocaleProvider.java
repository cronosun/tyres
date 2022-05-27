package com.github.cronosun.tyres.implementation;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface CurrentLocaleProvider {
  @Nullable
  Locale currentLocale();

  static CurrentLocaleProvider nullProvider() {
    return NullCurrentLocaleProvider.instance();
  }
}
