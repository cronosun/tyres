package com.github.cronosun.tyres.implementation;

import java.util.Locale;

public interface MessageFormatBackend {
  /**
   * The default implementation that uses {@link java.text.MessageFormat}.
   */
  static MessageFormatBackend defaultInstance() {
    return DefaultMessageFormatBackend.instance();
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

  /**
   * Validates the pattern. Also checks that the number of arguments match the given number of arguments. Throws
   * {@link com.github.cronosun.tyres.core.TyResException} is validation fails.
   */
  void validatePattern(String pattern, Locale locale, int numberOfArguments);
}
