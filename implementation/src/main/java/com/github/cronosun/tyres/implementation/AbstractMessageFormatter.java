package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

abstract class AbstractMessageFormatter implements MessageFormatter {

  public static MessageFormatter noCacheInstance() {
    return NoCacheMessageFormatter.INSTANCE;
  }

  @Override
  public String format(String pattern, Object[] args, Locale locale) {
    try {
      return formatInternal(pattern, args, locale);
    } catch (Exception exception) {
      throw new TyResException(
        "Invalid pattern or arguments, cannot format '" +
        pattern +
        "' (locale " +
        locale +
        ", arguments " +
        Arrays.toString(args) +
        "). See Java's MessageFormat documentation.",
        exception
      );
    }
  }

  @Override
  public void validatePattern(String pattern, Locale locale, int numberOfArguments) {
    final int numberOfArgumentsInPattern;
    try {
      numberOfArgumentsInPattern = validateAndReturnNumberOfArguments(pattern, locale);
    } catch (Exception exception) {
      throwInvalidPattern(exception, locale, pattern);
      return;
    }
    if (numberOfArgumentsInPattern != numberOfArguments) {
      throw new TyResException(
        "The pattern '" +
        pattern +
        "' (locale '" +
        locale.toLanguageTag() +
        "') has " +
        numberOfArgumentsInPattern +
        " arguments but the method in the bundle has " +
        numberOfArguments +
        "'. Either fix the pattern (also make sure it's valid) or fix the method in the bundle."
      );
    }
  }

  private void throwInvalidPattern(Exception exception, Locale locale, String pattern) {
    var baseMsg =
      "Invalid pattern '" +
      pattern +
      "', locale '" +
      locale.toLanguageTag() +
      "'. Make sure the pattern is correct (make sure escaping is correct, see Java's MessageFormat documentation; ' characters have to be escaped).";
    throw new TyResException(baseMsg, exception);
  }

  /**
   * Formats the given pattern. Throws an exception on error (invalid pattern, invalid arguments, ...).
   *
   */
  protected abstract String formatInternal(String pattern, Object[] args, Locale locale);

  /**
   * Validates the given pattern (throws an exception if pattern is invalid). If the pattern is valid, returns
   * the number of arguments in the pattern.
   */
  protected abstract int validateAndReturnNumberOfArguments(String pattern, Locale locale);

  private static final class NoCacheMessageFormatter extends AbstractMessageFormatter {

    private static final AbstractMessageFormatter INSTANCE = new NoCacheMessageFormatter();

    @Override
    protected String formatInternal(String pattern, Object[] args, Locale locale) {
      var format = new MessageFormat(pattern, locale);
      return format.format(args);
    }

    @Override
    protected int validateAndReturnNumberOfArguments(String pattern, Locale locale) {
      var format = new MessageFormat(pattern, locale);
      return format.getFormats().length;
    }
  }
}
