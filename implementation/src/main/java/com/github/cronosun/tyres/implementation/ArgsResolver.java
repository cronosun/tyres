package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.NotFoundConfig;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import javax.annotation.Nullable;

/**
 * Resolves arguments: Converts {@link Resolvable} to string.
 */
public interface ArgsResolver {
  static ArgsResolver defaultInstance() {
    return DefaultArgsResolver.instance();
  }

  /**
   * Resolves the given arguments.
   * <p>
   * Note 1: DOES NEVER modify the given arguments array. Returns a new array if at least one argument was resolved.
   * Note 2: The method is allowed to return the given args-array (if there are no arguments to resolve).
   * Note 3: Returns <code>null</code> if `notFoundConfig` is {@link NotFoundConfig.WithNullNoDefault#NULL} and at
   * least one argument could not be resolved.
   */
  @Nullable
  Object[] resolve(
    Resources resources,
    Locale locale,
    NotFoundConfig.WithNullNoDefault notFoundConfig,
    Object[] args
  );
}
