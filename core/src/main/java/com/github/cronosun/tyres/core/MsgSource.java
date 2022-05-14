package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface MsgSource {

    String message(MsgRes resource, NotFoundStrategy notFoundStrategy, Locale locale);

    default String message(MsgRes resource, Locale locale) {
        return message(resource, notFoundStrategy(), locale);
    }

    @Nullable
    String maybeMessage(MsgRes resource, Locale locale);

    @Nullable
    default String maybeMessage(Msg message, Locale locale) {
        return message.maybeMessage(this, locale);
    }

    default String message(Msg message, NotFoundStrategy notFoundStrategy, Locale locale) {
        return message.message(this, notFoundStrategy, locale);
    }

    default String message(Msg message, Locale locale) {
        return message.message(this, notFoundStrategy(), locale);
    }

    NotFoundStrategy notFoundStrategy();

    enum NotFoundStrategy {
        /**
         * If there's no such message, throws a {@link TyResException} exception.
         */
        THROW,
        /**
         * If there's no such message, returns the fallback value.
         */
        FALLBACK
    }
}
