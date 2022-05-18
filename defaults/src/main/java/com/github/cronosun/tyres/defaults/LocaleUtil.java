package com.github.cronosun.tyres.defaults;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class LocaleUtil {

  private LocaleUtil() {}

  @Nullable
  public static Locale getParent(Locale locale) {
    // language + "_" + country + "_" + (variant + "_#" | "#") + script + "_" + extensions
    if (locale.hasExtensions()) {
      return locale.stripExtensions();
    }
    if (!locale.getScript().isEmpty()) {
      return new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());
    }
    if (!locale.getVariant().isEmpty()) {
      return new Locale(locale.getLanguage(), locale.getCountry());
    }
    if (!locale.getCountry().isEmpty()) {
      return new Locale(locale.getLanguage());
    }
    if (!locale.getLanguage().isEmpty()) {
      return Locale.ROOT;
    }
    return null;
  }
}
