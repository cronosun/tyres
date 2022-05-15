package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface MsgSourceBackend {
  /**
   * Returns the message (if found).
   *
   * Error handling:
   * <ul>
   *     <li>If resource cannot be found: Method returns <code>null</code>.</li>
   *     <li>If resouce is invalid (invalid format / invalid number of arguments): Depending on throwOnError,
   *     either throws an error or returns <code>null</code></li>
   * </ul>
   */
  @Nullable
  String maybeMessage(Res<?> resource, Object[] args, Locale locale, boolean throwOnError);

  /**
   * Returns the default implementation that uses {@link java.util.ResourceBundle}.
   */
  static MsgSourceBackend usingResourceBundle() {
    return DefaultMsgSourceBackend.instance();
  }
}
