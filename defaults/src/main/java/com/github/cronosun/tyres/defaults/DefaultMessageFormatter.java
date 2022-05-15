package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgSource;
import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class DefaultMessageFormatter implements MessageFormatter {

  private static final DefaultMessageFormatter INSTANCE = new DefaultMessageFormatter();

  public static DefaultMessageFormatter instance() {
    return INSTANCE;
  }

  private DefaultMessageFormatter() {}

  @Override
  public String format(
    String pattern,
    Locale locale,
    Object[] args,
    MsgSource sourceForArguments,
    MsgSource.NotFoundStrategy notFoundStrategyForArguments
  ) throws TyResException {
    try {
      var format = new MessageFormat(pattern, locale);
      return format.format(args);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new TyResException(
        "Pattern '" + pattern + "' is invalid (given arguments: " + Arrays.toString(args) + ").",
        illegalArgumentException
      );
    }
  }

  @Nullable
  @Override
  public String maybeFormat(
    String pattern,
    Locale locale,
    Object[] args,
    MsgSource sourceForArguments
  ) {
    try {
      var format = new MessageFormat(pattern, locale);
      return format.format(args);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    }
  }
}
