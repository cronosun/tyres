package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.validation.ValidationError;
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

  @Override
  public ValidationError validateMessage(
    ResInfo.Str resInfo,
    String msgPattern,
    int numberOfArguments,
    Locale locale
  ) {
    MessageFormat msgFormat;
    try {
      msgFormat = new MessageFormat(msgPattern, locale);
    } catch (IllegalArgumentException invalidPattern) {
      return new ValidationError.InvalidMsgPattern(resInfo, locale, msgPattern);
    }
    var expectedNumberOfArguments = msgFormat.getFormats().length;
    if (numberOfArguments != expectedNumberOfArguments) {
      return new ValidationError.InvalidNumberOfArguments(
        resInfo,
        locale,
        msgPattern,
        numberOfArguments,
        expectedNumberOfArguments
      );
    } else {
      return null;
    }
  }
}
