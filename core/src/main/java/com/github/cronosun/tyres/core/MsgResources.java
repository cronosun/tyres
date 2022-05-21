package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface MsgResources {
  /**
   * Returns the message.
   * <p>
   * If there's no such resource: Depending on {@link MsgNotFoundStrategy}, either returns the fallback message
   * or throws {@link TyResException}.
   */
  String msg(MsgRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale);

  /**
   * Calls {@link #msg(MsgRes, MsgNotFoundStrategy, Locale)} with {@link #msgNotFoundStrategy()}.
   */
  default String msg(MsgRes resource, Locale locale) {
    return msg(resource, msgNotFoundStrategy(), locale);
  }

  /**
   * Returns the message (if the resource can be found) or <code>null</code> if there's no such message.
   * <p>
   * Note: Expect the implementation to throw {@link TyResException} if something is wrong (such as an invalid
   * message format or invalid arguments).
   */
  @Nullable
  String maybeMsg(MsgRes resource, Locale locale);

  /**
   * Calls {@link Msg#maybeMsg(MsgResources, Locale)}.
   */
  @Nullable
  default String maybeResolveMsg(Msg message, Locale locale) {
    return message.maybeMsg(this, locale);
  }

  /**
   * Calls {@link Msg#msg(MsgResources, MsgNotFoundStrategy, Locale)}.
   */
  default String resolveMsg(Msg message, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return message.msg(this, notFoundStrategy, locale);
  }

  /**
   * Calls {@link Msg#msg(MsgResources, MsgNotFoundStrategy, Locale)} with {@link #msgNotFoundStrategy()}.
   */
  default String resolveMsg(Msg message, Locale locale) {
    return message.msg(this, msgNotFoundStrategy(), locale);
  }

  /**
   * What {@link #msg(MsgRes, Locale)} should do if the resouce cannot be found.
   */
  MsgNotFoundStrategy msgNotFoundStrategy();

  /**
   * Generates the fallback message (note, this is not to be confused with the default message,
   * see {@link ResInfoDetails.StringResource#defaultValue()}).
   */
  String fallbackFor(ResInfo resInfo, Object[] args);
}
