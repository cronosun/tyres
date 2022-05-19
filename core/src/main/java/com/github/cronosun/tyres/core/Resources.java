package com.github.cronosun.tyres.core;

import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Resources {
  /**
   * Returns the message.
   * <p>
   * If there's no such resouce: Depending on {@link MsgNotFoundStrategy}, either returns the fallback message
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
   * Calls {@link Msg#maybeMsg(Resources, Locale)}.
   */
  @Nullable
  default String maybeResolveMsg(Msg message, Locale locale) {
    return message.maybeMsg(this, locale);
  }

  /**
   * Calls {@link Msg#msg(Resources, MsgNotFoundStrategy, Locale)}.
   */
  default String resolveMsg(Msg message, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return message.msg(this, notFoundStrategy, locale);
  }

  /**
   * Calls {@link Msg#msg(Resources, MsgNotFoundStrategy, Locale)} with {@link #msgNotFoundStrategy()}.
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

  /**
   * Returns the string from the resources if found. Returns <code>null</code> if given string resource cannot
   * be found.
   */
  @Nullable
  String maybeStr(StrRes resource, Locale locale);

  /**
   * Returns the string from the resources, if found. Throws a {@link TyResException} if the given string resource
   * cannot be resolved.
   */
  String str(StrRes resource, Locale locale);

  /**
   * Returns the binary as input stream or <code>null</code> if resource cannot be found.
   */
  @Nullable
  InputStream maybeBin(BinRes resource, Locale locale);

  InputStream bin(BinRes resource, Locale locale);
}
