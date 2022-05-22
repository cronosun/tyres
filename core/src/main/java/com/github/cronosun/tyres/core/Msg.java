package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Something that can translate itself using the given {@link MsgResources}.
 */
@ThreadSafe
public interface Msg extends WithConciseDebugString {
  String msg(MsgResources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale);

  @Nullable
  String maybeMsg(MsgResources resources, Locale locale);
}
