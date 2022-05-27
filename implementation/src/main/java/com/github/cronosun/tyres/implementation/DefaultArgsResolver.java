package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.NotFoundConfig;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import java.util.Objects;

final class DefaultArgsResolver implements ArgsResolver {

  private static final DefaultArgsResolver INSTANCE = new DefaultArgsResolver();

  private DefaultArgsResolver() {}

  public static DefaultArgsResolver instance() {
    return INSTANCE;
  }

  @Override
  public Object[] resolve(
    Resources resources,
    Locale locale,
    NotFoundConfig.WithNullNoDefault notFoundConfig,
    Object[] args
  ) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return args;
    }

    Object[] newArgs = null;
    for (var index = 0; index < numberOfArgs; index++) {
      var original = args[0];
      if (original instanceof Resolvable) {
        var resolvable = (Resolvable) original;
        if (newArgs == null) {
          newArgs = args.clone();
        }
        var resolvedValue = resolvable
          .resolve(resources)
          .getText(locale, notFoundConfig.withNullAndDefault());
        if (resolvedValue == null) {
          // not able to resolve one argument means: Can't resolve the entire message
          return null;
        }
        newArgs[index] = resolvedValue;
      }
    }
    return Objects.requireNonNullElse(newArgs, args);
  }
}
