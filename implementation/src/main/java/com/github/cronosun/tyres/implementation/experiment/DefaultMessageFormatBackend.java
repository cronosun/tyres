package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

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
        ").",
        exception
      );
    }
  }
}
