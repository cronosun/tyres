package com.github.cronosun.tyres.core.experiment;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface Resources2 {
  /**
   * Returns the bundle instance.
   * <p>
   * Can throw {@link com.github.cronosun.tyres.core.TyResException} if the bundle is invalid.
   */
  <T> T get(Class<T> bundleClass);

  default Text resolve(Resolvable resolvable) {
    return resolvable.resolve(this);
  }

  /**
   * Returns the current locale - if any. Returns <code>null</code> if there's no current locale.
   */
  @Nullable
  Locale currentLocale();

  /**
   * Returns what to do if a resource cannot be found: Throw or return the fallback resource.
   */
  DefaultNotFoundConfig defaultNotFoundConfig();
}
