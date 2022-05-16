package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Something that can translate itself using the given {@link Resources}.
 *
 * It's also a maker for the resource type, see {@link Res}.
 */
@ThreadSafe
public interface Msg {
  String message(Resources resources, Resources.NotFoundStrategy notFoundStrategy, Locale locale);

  @Nullable
  String maybeMessage(Resources resources, Locale locale);
}
