package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface StrResources {
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
}
