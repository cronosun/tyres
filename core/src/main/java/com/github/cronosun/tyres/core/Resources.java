package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Resources {
  String message(MsgRes resource, NotFoundStrategy notFoundStrategy, Locale locale);

  default String message(MsgRes resource, Locale locale) {
    return message(resource, notFoundStrategy(), locale);
  }

  @Nullable
  String maybeMessage(MsgRes resource, Locale locale);

  @Nullable
  default String maybeMessage(Msg message, Locale locale) {
    return message.maybeMessage(this, locale);
  }

  default String message(Msg message, NotFoundStrategy notFoundStrategy, Locale locale) {
    return message.message(this, notFoundStrategy, locale);
  }

  default String message(Msg message, Locale locale) {
    return message.message(this, notFoundStrategy(), locale);
  }

  NotFoundStrategy notFoundStrategy();

  /**
   * Generates the fallback message (note, this is not to be confused with the default message,
   * see {@link ResInfoDetails.StringResource#defaultValue()}).
   */
  String fallbackFor(ResInfo resInfo, Object[] args);

  enum NotFoundStrategy {
    /**
     * If there's no such message, throws a {@link TyResException} exception.
     */
    THROW,
    /**
     * If there's no such message, returns the fallback value.
     */
    FALLBACK,
  }
}
