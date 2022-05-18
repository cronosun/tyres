package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Backend for string-like sources: plain strings and messages.
 */
@ThreadSafe
public interface StringBackend {
  /**
   * Returns the formatted message (if found).
   *
   * Error handling:
   * <ul>
   *     <li>If resource cannot be found: Method returns <code>null</code>.</li>
   *     <li>If resouce is invalid (invalid format / invalid arguments): Depending on throwOnError,
   *     either throws an error or returns <code>null</code>. If the implementation is unable to detect
   *     errors, it can (but should not) return a broken message.</li>
   * </ul>
   *
   * @param args The arguments (never null; but can be empty): The arguments are already resolved,
   *             implementations MUST NOT try to resolve them.
   */
  @Nullable
  String maybeMessage(Res<?> resource, Object[] args, Locale locale, boolean throwOnError);

  /**
   * Returns the default implementation that uses {@link java.util.ResourceBundle}.
   */
  static StringBackend usingResourceBundle() {
    return DefaultStringBackend.instance();
  }
}
