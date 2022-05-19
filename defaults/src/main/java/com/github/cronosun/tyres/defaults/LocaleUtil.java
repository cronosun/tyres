package com.github.cronosun.tyres.defaults;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

final class LocaleUtil {

  private static final ResourceBundle.Control RES_BUNDLE_CONTROL = ResourceBundle.Control.getControl(
    FORMAT_PROPERTIES
  );

  private LocaleUtil() {}

  public static List<Locale> getCandidateLocales(Locale locale) {
    return RES_BUNDLE_CONTROL.getCandidateLocales("", locale);
  }
}
