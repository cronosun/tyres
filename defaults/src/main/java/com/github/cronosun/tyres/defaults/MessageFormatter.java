package com.github.cronosun.tyres.defaults;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface MessageFormatter {
  /**
   * Formats the given message.
   *
   * Error handling: If the pattern is invalid or the given arguments do not match, the implementation
   * should behave like this:
   * <ul>
   *     <li>if throwOnError is false: SHOULD return <code>null</code> (if possible) or return a
   *     broken message (if the implementation is not able to detect errors). MUST NOT throw.</li>
   *     <li>if throwOnError is true: SHULD throw {@link com.github.cronosun.tyres.core.TyResException},
   *     alternatively (if the implementation is unable to detect errors), return a broken message.
   *     MUST NOT return <code>null</code>.</li>
   * </ul>
   *
   * @param args Note: Arguments are already resolved here (implementation MUST NOT resolve them).
   */
  @Nullable
  String format(String pattern, Object[] args, Locale locale, boolean throwOnError);

  static MessageFormatter defaultImplementaion() {
    return DefaultMessageFormatter.instance();
  }
}
