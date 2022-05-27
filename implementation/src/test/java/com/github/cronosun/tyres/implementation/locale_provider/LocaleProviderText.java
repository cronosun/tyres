package com.github.cronosun.tyres.implementation.locale_provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class LocaleProviderTest {

  @Test
  void testLocaleProvider() {
    var localeProvider = new DemoLocaleProvider();
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW, localeProvider);
    var bundle = resources.get(LocaleProviderBundle.class);

    // locale provider has no current locale. Won't get a message
    assertNull(bundle.someText().maybe());

    // but if we specify the locale, there are texts
    assertEquals("Italian", bundle.someText().maybe(Locale.ITALIAN));
    assertEquals("German", bundle.someText().maybe(Locale.GERMAN));
    assertEquals("English", bundle.someText().maybe(Locale.ENGLISH));

    // now set the locale
    localeProvider.setCurrentLocale(Locale.ITALIAN);
    assertEquals("Italian", bundle.someText().maybe());
    localeProvider.setCurrentLocale(Locale.GERMAN);
    assertEquals("German", bundle.someText().maybe());
    localeProvider.setCurrentLocale(Locale.ENGLISH);
    assertEquals("English", bundle.someText().maybe());
  }
}
