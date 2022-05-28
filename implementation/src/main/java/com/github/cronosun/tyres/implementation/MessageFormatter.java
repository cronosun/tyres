package com.github.cronosun.tyres.implementation;

import java.util.Locale;

public interface MessageFormatter {
  /**
   * The default implementation that uses {@link java.text.MessageFormat} and does NOT cache the
   * {@link java.text.MessageFormat} (you usually want to cache them).
   *
   * @see #newCachedMessageFormatter()
   */
  static MessageFormatter noCacheDefaultInstance() {
    return AbstractMessageFormatter.noCacheInstance();
  }

  /**
   * Returns a new instance of the cached message formatter (internally using {@link java.text.MessageFormat}). You
   * usually want this, unless your application loads and unloads parts dynamically, since the cache
   * does never evict cached {@link java.text.MessageFormat}s.
   */
  static MessageFormatter newCachedMessageFormatter() {
    return new CachedMessageFormatter();
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
