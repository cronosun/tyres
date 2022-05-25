package com.github.cronosun.tyres.core.experiment;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface Text extends Resolvable {
    String get(@Nullable Locale locale, NotFoundConfig notFoundConfig);

    default String get(Locale locale) {
        return get(locale, NotFoundConfig.DEFAULT);
    }

    default String get() {
        return get(null, NotFoundConfig.DEFAULT);
    }

    default String get(NotFoundConfig notFoundConfig) {
        return get(null, notFoundConfig);
    }

    @Nullable
    default String maybe() {
        return maybe(null);
    }

    @Nullable
    String maybe(@Nullable Locale locale);

    @Override
    default Text get(NewResouces resouces) {
        return this;
    }
}
