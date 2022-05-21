package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Message resources.
 * <p>
 * Note: Message resources internally are based on {@link StrResources}: This means that if you get a string from
 * {@link StrResources} with a {@link ResInfo} that's a {@link ResInfo} from a message, you'll get a result too
 * (the message pattern).
 */
public interface MsgResources {
  /**
   * Returns the message.
   * <p>
   * If there's no such resource: Depending on {@link MsgNotFoundStrategy}, either returns the fallback message
   * or throws {@link TyResException}.
   */
  String get(MsgRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale);

  /**
   * Calls {@link #get(MsgRes, MsgNotFoundStrategy, Locale)} with {@link #notFoundStrategy()}.
   */
  default String get(MsgRes resource, Locale locale) {
    return get(resource, notFoundStrategy(), locale);
  }

  /**
   * Returns the message (if the resource can be found) or <code>null</code> if there's no such message.
   * <p>
   * Note: Expect the implementation to throw {@link TyResException} if something is wrong (such as an invalid
   * message format or invalid arguments).
   */
  @Nullable
  String maybe(MsgRes resource, Locale locale);

  /**
   * Calls {@link Msg#maybeMsg(MsgResources, Locale)}.
   */
  @Nullable
  default String maybeResolve(Msg message, Locale locale) {
    return message.maybeMsg(this, locale);
  }

  /**
   * Calls {@link Msg#msg(MsgResources, MsgNotFoundStrategy, Locale)}.
   */
  default String resolve(Msg message, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return message.msg(this, notFoundStrategy, locale);
  }

  /**
   * Calls {@link Msg#msg(MsgResources, MsgNotFoundStrategy, Locale)} with {@link #notFoundStrategy()}.
   */
  default String resolve(Msg message, Locale locale) {
    return message.msg(this, notFoundStrategy(), locale);
  }

  /**
   * What {@link #get(MsgRes, Locale)} should do if the resouce cannot be found.
   */
  MsgNotFoundStrategy notFoundStrategy();

  /**
   * Generates the fallback message (note, this is not to be confused with the default message,
   * see {@link ResInfoDetails.StrResource#defaultValue()}).
   */
  String fallbackFor(ResInfo resInfo, Object[] args);
}
