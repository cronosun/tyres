package com.github.cronosun.tyres.defaults;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.jetbrains.annotations.Nullable;

final class LocaleUtil {

  private static final ResourceBundle.Control RES_BUNDLE_CONTROL = ResourceBundle.Control.getControl(
    FORMAT_PROPERTIES
  );

  private LocaleUtil() {}

  public static List<Locale> getCandidateLocales(Locale locale) {
    return RES_BUNDLE_CONTROL.getCandidateLocales("", locale);
  }

  @Nullable
  @Deprecated
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
