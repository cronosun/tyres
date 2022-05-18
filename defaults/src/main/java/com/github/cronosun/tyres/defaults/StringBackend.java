package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Backend for string-like sources: plain strings and messages.
 */
@ThreadSafe
public interface StringBackend {
  /**
   * Returns the formatted message (if found).
   * <p>
   * Error handling:
   * <ul>
   *     <li>If resource cannot be found: Method returns <code>null</code>.</li>
   *     <li>If resource is invalid (invalid format / invalid arguments / not a string resource): Depending on
   *     throwOnError, either throws an error or returns <code>null</code>. If the implementation is unable to detect
   *     errors, it can (but should not) return a broken message.</li>
   * </ul>
   *
   * @param args The arguments (never null; but can be empty): The arguments are already resolved,
   *             implementations MUST NOT try to resolve them.
   */
  @Nullable
  String maybeMessage(ResInfo resInfo, Object[] args, Locale locale, boolean throwOnError);

  /**
   * Returns the plain string (not formatted) - if found. If there's no such resource, returns <code>null</code>.
   * <p>
   * Error handling: In general, returns <code>null</code> if the resource cannot be retrieved, except when
   * throwOnError is true and the resource is not a {@link com.github.cronosun.tyres.core.ResInfoDetails.Kind#STRING}
   * resource (in that case, a {@link TyResException} is thrown).
   */
  @Nullable
  String maybeString(ResInfo resInfo, Locale locale, boolean throwOnError);

  /**
   * Returns the default implementation that uses {@link java.util.ResourceBundle}.
   */
  static StringBackend usingResourceBundle() {
    return DefaultStringBackend.instance();
  }
}
