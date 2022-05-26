package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class DefaultMessageFormatBackend implements MessageFormatBackend {

  private static final DefaultMessageFormatBackend INSTANCE = new DefaultMessageFormatBackend();

  public static DefaultMessageFormatBackend instance() {
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
        "). See Java's MessageFormat documentation.",
        exception
      );
    }
  }

  @Override
  public void validatePattern(String pattern, Locale locale, int numberOfArguments) {
    final int numberOfArgumentsInPattern;
    try {
      var format = new MessageFormat(pattern, locale);
      numberOfArgumentsInPattern = format.getFormats().length;
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
        " arguments but the method in the bundly has " +
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
}
