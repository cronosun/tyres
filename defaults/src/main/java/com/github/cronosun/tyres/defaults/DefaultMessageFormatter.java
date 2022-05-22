package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

@ThreadSafe
final class DefaultMessageFormatter implements MessageFormatter {

  private static final DefaultMessageFormatter INSTANCE = new DefaultMessageFormatter();

  private DefaultMessageFormatter() {}

  public static DefaultMessageFormatter instance() {
    return INSTANCE;
  }

  @Override
  public String format(String pattern, Object[] args, Locale locale) {
    try {
      var format = new MessageFormat(pattern, locale);
      return format.format(args);
    } catch (Exception exception) {
      throw new TyResException(
        "Invalid pattern or arguments, cannot format '" +
        pattern +
        "' (locale " +
        locale +
        ", arguments " +
        Arrays.toString(args) +
        ").",
        exception
      );
    }
  }
}
