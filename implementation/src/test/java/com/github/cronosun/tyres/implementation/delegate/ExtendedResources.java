package com.github.cronosun.tyres.implementation.delegate;

import com.github.cronosun.tyres.core.experiment.*;

import java.util.Locale;
import java.util.Set;

/**
 * You can extend {@link Resources2} in your application using {@link DelegatingResources}.
 */
public final class ExtendedResources extends DelegatingResources {

  private final Set<Locale> supportedLocales = Set.of(Locale.GERMANY, Locale.US, Locale.FRANCE);

  public ExtendedResources(Resources2 resources) {
    super(resources);
  }

  public Localized toLocalized(Text text) {
    return Localized.fromText(text, supportedLocales);
  }

  public int asInt(Text resource, Locale locale) {
    var asString = resource.get(locale, NotFoundConfig.THROW);
    return Integer.parseInt(asString);
  }
}
