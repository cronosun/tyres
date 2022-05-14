package com.github.cronosun.tyres.defaults;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Resource bundle, provides more or less the same functionality as {@link java.util.ResourceBundle}.
 */
public interface TyResResourceBundle {
    /**
     * Returns the given string from the bundle. Returns <code>null</code> if there's no such string in this
     * bundle or if it's not a string.
     */
    @Nullable
    String getString(String key, Locale locale);
}
