package com.github.cronosun.tyres.defaults;

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
   * Returns the default implementation that uses {@link java.util.ResourceBundle}.
   */
  static StringBackend usingResourceBundle() {
    return DefaultStringBackend.instance();
  }

  /**
   * Returns the formatted message (if found).
   * <p>
   * Does not throw if the resource cannot be found - but expect the implementaton to throw @{@link TyResException} if
   * something else is wrong, like invalid arguments or an invalid message (a message that cannot be parsed).
   *
   * @param args The arguments (never null; but can be empty): The arguments are already resolved,
   *             implementations MUST NOT try to resolve them.
   */
  @Nullable
  String maybeMessage(ResInfo resInfo, Object[] args, Locale locale);

  /**
   * Returns the plain string (not formatted) - if found. If there's no such resource, returns <code>null</code>.
   * <p>
   * Does not throw if the resouce cannot be found.
   */
  @Nullable
  String maybeString(ResInfo resInfo, Locale locale);
}
