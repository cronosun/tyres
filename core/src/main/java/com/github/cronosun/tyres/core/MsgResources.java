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
   * Calls {@link #get(MsgRes, MsgNotFoundStrategy, Locale)} with {@link Resources#notFoundStrategy()}.
   */
  String get(MsgRes resource, Locale locale);

  /**
   * Returns the message (if the resource can be found) or <code>null</code> if there's no such message.
   * <p>
   * Note: Expect the implementation to throw {@link TyResException} if something is wrong (such as an invalid
   * message format or invalid arguments).
   */
  @Nullable
  String maybe(MsgRes resource, Locale locale);
}
