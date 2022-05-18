package com.github.cronosun.tyres.defaults;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface MessageFormatter {
  /**
   * Formats the given message.
   *
   * Errors: Might throw {@link com.github.cronosun.tyres.core.TyResException} if the pattern is invalid or the
   * arguments are invalid (too many arguments, too few arguments or argument cannot be formatted).
   *
   * @param args Note: Arguments are already resolved here (implementation MUST NOT resolve them).
   */
  String format(String pattern, Object[] args, Locale locale);

  static MessageFormatter defaultImplementaion() {
    return DefaultMessageFormatter.instance();
  }
}
