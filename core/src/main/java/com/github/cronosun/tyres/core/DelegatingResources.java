package com.github.cronosun.tyres.core;

import java.util.Locale;
import javax.annotation.Nullable;

public class DelegatingResources implements Resources {

  private final Resources resources;

  public DelegatingResources(Resources resources) {
    this.resources = resources;
  }

  @Override
  public final Text resolve(Resolvable resolvable) {
    return resources.resolve(resolvable);
  }

  @Override
  public final <T> T get(Class<T> bundleClass) {
    return resources.get(bundleClass);
  }

  @Override
  public final @Nullable Locale currentLocale() {
    return resources.currentLocale();
  }

  @Override
  public final DefaultNotFoundConfig defaultNotFoundConfig() {
    return resources.defaultNotFoundConfig();
  }
}
