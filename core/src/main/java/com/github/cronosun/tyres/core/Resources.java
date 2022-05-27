package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Resources {
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

  /**
   * Validates the given bundle for the given locale. Throws {@link com.github.cronosun.tyres.core.TyResException}
   * if the bundle does not validate.
   *
   * Note: This is highly implementation specific. Implementations must at least check whether {@link #get(Class)}
   * works. Implementations - however - should do more:
   *
   * <ul>
   *     <li>Assert all resouces are available for the given locale (unless marked as optional, see {@link com.github.cronosun.tyres.core.Validation})</li>
   * <li>Assert that there are no unused/superflous resouces.</li>
   * <li>Assert that all patterns for the messages are valid.</li>
   * </ul>
   */
  default void validate(Class<?> bundleClass, Locale locale) {
    get(bundleClass);
  }

  default void validate(Class<?> bundleClass, Iterable<Locale> locales) {
    for (var locale : locales) {
      validate(bundleClass, locale);
    }
  }
}
