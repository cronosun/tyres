package com.github.cronosun.tyres.implementation.locale_provider;

import com.github.cronosun.tyres.implementation.CurrentLocaleProvider;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * This will usually get the current locale from either {@link Locale#getDefault()} or from the current HTTP
 * request header.
 */
public class DemoLocaleProvider implements CurrentLocaleProvider {

  @Nullable
  private Locale currentLocale;

  @Override
  public @Nullable Locale currentLocale() {
    return currentLocale;
  }

  public void setCurrentLocale(@Nullable Locale currentLocale) {
    this.currentLocale = currentLocale;
  }
}
