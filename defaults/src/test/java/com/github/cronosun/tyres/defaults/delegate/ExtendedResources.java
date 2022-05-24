package com.github.cronosun.tyres.defaults.delegate;

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

  public void validate(Object bundle) {
    var validationError = common().validate(bundle, this.supportedLocales);
    if (validationError != null) {
      throw new TyResException(validationError);
    }
  }

  public LocalizedMsg toLocalizedMsg(Resolvable resolvable) {
    return LocalizedMsg.fromResources(
      this,
      resolvable,
      LocalizedMsg.FromResourcesConfig.DEFAULT_NOT_FOUND_STRATEGY,
      supportedLocales
    );
  }

  public int asInt(StrRes resource, Locale locale) {
    var asString = str().get(resource, MsgNotFoundStrategy.THROW, locale);
    return Integer.parseInt(asString);
  }
}
