package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Resource bundle, provides more or less the same functionality as {@link java.util.ResourceBundle}.
 * <p>
 * Implementations of this class must be thread-safe.
 */
@ThreadSafe
public interface TyResResourceBundle {
  /**
   * Returns the given resource from the bundle. Returns <code>null</code> if there's no such entry in this
   * bundle or if it's not a string.
   */
  @Nullable
  String getString(ResInfo resInfo, Locale locale);

  /**
   * Returns information about this instance for debugging.
   */
  @Override
  String toString();

  /**
   * Returns a short and concise reference for a developer to identify this bundle (it's used for error messages).
   */
  String debugReference();
}
