package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

// TODO: Sollten mit marker zeugs arbeiten... MsgMarker, StrMarker (also 2. Generisches argument bei Res<TSelf, Marker>
@ThreadSafe
public interface Resources {
  String msg(Res<?, MsgMarker> resource, NotFoundStrategy notFoundStrategy, Locale locale);

  default String msg(Res<?, MsgMarker> resource, Locale locale) {
    return msg(resource, msgNotFoundStrategy(), locale);
  }

  @Nullable
  String maybeMsg(Res<?, MsgMarker> resource, Locale locale);

  @Nullable
  default String maybeResolveMsg(Msg message, Locale locale) {
    return message.maybeMessage(this, locale);
  }

  default String resolveMsg(Msg message, NotFoundStrategy notFoundStrategy, Locale locale) {
    return message.message(this, notFoundStrategy, locale);
  }

  default String resolveMsg(Msg message, Locale locale) {
    return message.message(this, msgNotFoundStrategy(), locale);
  }

  NotFoundStrategy msgNotFoundStrategy();

  /**
   * Returns the string from the resources if found. Returns <code>null</code> if given string resource cannot
   * be found.
   */
  @Nullable
  String maybeStr(Res<?, StrMarker> resource, Locale locale);

  /**
   * Returns the string from the resources, if found. Throws a {@link TyResException} if the given string resource
   * cannot be resolved.
   */
  String str(Res<?, StrMarker> resource, Locale locale);

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
