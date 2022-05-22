package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;

@ThreadSafe
public interface MessageFormatter {
  static MessageFormatter defaultImplementation() {
    return DefaultMessageFormatter.instance();
  }

  /**
   * Formats the given message.
   * <p>
   * Errors: Might throw {@link com.github.cronosun.tyres.core.TyResException} if the pattern is invalid or the
   * arguments are invalid (too many arguments, too few arguments or argument cannot be formatted).
   *
   * @param args Note: Arguments are already resolved here (implementation MUST NOT resolve them).
   */
  String format(String pattern, Object[] args, Locale locale);
}
