package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgSource;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface MessageFormatter {
  /**
   * Formats the given message.
   * <p>
   * Note: Arguments of type {@link com.github.cronosun.tyres.core.Msg} are resolved. <--- TODO: Nein
   * <p>
   * Throws {@link TyResException} if the pattern is invalid and/or the given arguments are not compatible
   * with the given pattern.
   */
  String format(
    String pattern,
    Locale locale,
    Object[] args,
    MsgSource sourceForArguments,
    MsgSource.NotFoundStrategy notFoundStrategyForArguments
  ) throws TyResException;

  /**
   * See {@link #format(String, Locale, Object[], MsgSource, MsgSource.NotFoundStrategy)} but returns
   * <code>null</code> instead of throwing an exception if formatting fails.
   */
  @Nullable
  String maybeFormat(String pattern, Locale locale, Object[] args, MsgSource sourceForArguments);
}
