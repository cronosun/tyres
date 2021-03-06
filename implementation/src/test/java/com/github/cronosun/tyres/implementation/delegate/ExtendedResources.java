package com.github.cronosun.tyres.implementation.delegate;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Set;

/**
 * You can extend {@link Resources} in your application using {@link DelegatingResources}.
 */
public final class ExtendedResources extends DelegatingResources {

  private final Set<Locale> supportedLocales = Set.of(Locale.GERMANY, Locale.US, Locale.FRANCE);

  public ExtendedResources(Resources resources) {
    super(resources);
  }

  public Localized toLocalized(Text text) {
    return Localized.fromText(text, supportedLocales);
  }

  public int asInt(Text resource, Locale locale) {
    var asString = resource.get(locale, NotFoundConfig.THROW);
    return Integer.parseInt(asString);
  }

  public void validate(Class<?> bundleClass) {
    for (var locale : supportedLocales) {
      this.validate(bundleClass, locale);
    }
  }
}
