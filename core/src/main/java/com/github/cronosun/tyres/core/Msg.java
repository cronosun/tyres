package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Something that can translate itself using the given {@link MsgSource}.
 */
public interface Msg {
    String message(MsgSource source, MsgSource.NotFoundStrategy notFoundStrategy, Locale locale);

    @Nullable
    String maybeMessage(MsgSource source, Locale locale);
}
