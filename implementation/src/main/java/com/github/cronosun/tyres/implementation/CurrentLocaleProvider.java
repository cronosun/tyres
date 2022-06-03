package com.github.cronosun.tyres.implementation;

import java.util.Locale;
import javax.annotation.Nullable;

public interface CurrentLocaleProvider {
  /**
   * The implementation that always returns <code>null</code> for {@link CurrentLocaleProvider#currentLocale()}.
   */
  static CurrentLocaleProvider nullProvider() {
    return NullCurrentLocaleProvider.instance();
  }

  /**
   * Returns the current locale or <code>null</code> if there's none.
   * <p>
   * The current locale is typically determined by calling {@link Locale#getDefault()} or by getting the information
   * from the current HTTP request header (if it's a web application).
   */
  @Nullable
  Locale currentLocale();
}
