package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

final class DefaultMessageFormatter implements MessageFormatter {

  private static final Logger LOGGER = Logger.getLogger(DefaultMessageFormatter.class.getName());

  private DefaultMessageFormatter() {}

  private static final DefaultMessageFormatter INSTANCE = new DefaultMessageFormatter();

  public static DefaultMessageFormatter instance() {
    return INSTANCE;
  }

  @Override
  public @Nullable String format(
    String pattern,
    Object[] args,
    Locale locale,
    boolean throwOnError
  ) {
    try {
      var format = new MessageFormat(pattern, locale);
      return format.format(args);
    } catch (IllegalArgumentException iae) {
      if (throwOnError) {
        throw new TyResException(
          "Invalid pattern or arguments, cannot format '" +
          pattern +
          "' (locale " +
          locale +
          ", arguments " +
          Arrays.toString(args) +
          ").",
          iae
        );
      } else {
        LOGGER.log(Level.INFO, "Invalid format", iae);
        return null;
      }
    }
  }
}
