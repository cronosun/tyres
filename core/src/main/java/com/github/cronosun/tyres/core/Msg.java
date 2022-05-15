package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Something that can translate itself using the given {@link MsgSource}.
 */
@ThreadSafe
public interface Msg {
  String message(MsgSource source, MsgSource.NotFoundStrategy notFoundStrategy, Locale locale);

  @Nullable
  String maybeMessage(MsgSource source, Locale locale);
}
