package com.github.cronosun.tyres.defaults.delegate;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.defaults.LocalizedMsg;
import com.github.cronosun.tyres.defaults.DelegatingResouces;
import java.util.Locale;
import java.util.Set;

/**
 * You can extend {@link Resources} in your application using {@link DelegatingResouces}.
 */
public final class ExtendedResources extends DelegatingResouces {

  private final Set<Locale> supportedLocales = Set.of(Locale.GERMANY, Locale.US, Locale.FRANCE);

  public ExtendedResources(Resources resources) {
    super(resources);
  }

  public void validate(Object bundle) {
    common().validate(bundle, this.supportedLocales);
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
