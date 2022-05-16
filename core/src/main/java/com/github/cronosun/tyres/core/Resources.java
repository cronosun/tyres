package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Resources {
  String message(
    Resolvable<? extends Msg> resolvable,
    NotFoundStrategy notFoundStrategy,
    Locale locale
  );

  default String message(Resolvable<? extends Msg> resolvable, Locale locale) {
    return message(resolvable, notFoundStrategy(), locale);
  }

  @Nullable
  String maybeMessage(Resolvable<? extends Msg> resolvable, Locale locale);

  /*
  @Nullable
  default String maybeMessage(Resolvable<Msg> message, Locale locale) {
    var resource = message.resource();
    if (resource!=null) {
      return maybeMessage(resource, locale);
    } else {
      return message.resolvable().maybeMessage(this,locale);
    }
  }

  default String message(Resolvable<Msg> message, NotFoundStrategy notFoundStrategy, Locale locale) {
    var resource = message.resource();
    if (resource!=null) {

    }
    return message.message(this, notFoundStrategy, locale);
  }

  default String message(Resolvable<Msg> message, Locale locale) {
    return message.message(this, notFoundStrategy(), locale);
  }*/

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
