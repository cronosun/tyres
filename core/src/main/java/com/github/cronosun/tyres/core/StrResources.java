package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface StrResources {
  /**
   * Returns the string from the resources if found. Returns <code>null</code> if given string resource cannot
   * be found.
   */
  @Nullable
  String maybe(StrRes resource, Locale locale);

  /**
   * Calls {@link #get(StrRes, MsgNotFoundStrategy, Locale)} with {@link Resources#notFoundStrategy()}.
   */
  String get(StrRes resource, Locale locale);

  /**
   * Returns the string from the resources, if found. If not found, either throws {@link TyResException} or
   * returns a fallback string, depending on {@link MsgNotFoundStrategy}.
   */
  String get(StrRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale);
}
