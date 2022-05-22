package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface Resolver {
  /**
   * Calls {@link Resolvable#maybe(Resources, Locale)}}.
   */
  @Nullable
  String maybe(Resolvable message, Locale locale);

  /**
   * Calls {@link Resolvable#get(Resources, MsgNotFoundStrategy, Locale)}}.
   */
  String get(Resolvable message, MsgNotFoundStrategy notFoundStrategy, Locale locale);

  /**
   * Calls {@link Resolvable#get(Resources, MsgNotFoundStrategy, Locale)}} with {@link Resources#notFoundStrategy()}.
   */
  String get(Resolvable message, Locale locale);
}
