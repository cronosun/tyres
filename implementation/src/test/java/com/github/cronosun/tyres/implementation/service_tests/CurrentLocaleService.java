package com.github.cronosun.tyres.implementation.service_tests;

import java.util.Locale;

/**
 * In a real world application, this could take the locale from the HTTP request header. For this demonstration,
 * it's just hardcoded.
 */
public class CurrentLocaleService {

  private Locale locale = Locale.ENGLISH;

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
