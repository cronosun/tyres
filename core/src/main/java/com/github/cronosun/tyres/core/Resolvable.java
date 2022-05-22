package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Something that can translate itself using the given {@link Resources}.
 */
@ThreadSafe
public interface Resolvable extends WithConciseDebugString {
  String get(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale);

  @Nullable
  String maybe(Resources resources, Locale locale);
}
