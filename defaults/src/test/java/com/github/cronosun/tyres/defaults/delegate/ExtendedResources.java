package com.github.cronosun.tyres.defaults.delegate;

import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.defaults.ResourcesDelegate;
import java.util.Locale;
import java.util.Set;

/**
 * You can extend {@link Resources} in your application using {@link ResourcesDelegate}.
 */
public final class ExtendedResources extends ResourcesDelegate {

  private final Set<Locale> supportedLocales = Set.of(Locale.GERMAN, Locale.ENGLISH);

  public ExtendedResources(Resources resources) {
    super(resources);
  }

  public void validate(Object bundle) {
    validate(bundle, this.supportedLocales);
  }

  public int asInt(StrRes resource, Locale locale) {
    var asString = str().get(resource, locale);
    return Integer.parseInt(asString);
  }
}
