package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import com.github.cronosun.tyres.core.experiment.Resources2;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

// TODO: Use...

/**
 * Resolves arguments: Converts {@link com.github.cronosun.tyres.core.experiment.Resolvable} to string.
 */
public interface ArgsResolver {

    /**
     * Resolves the given arguments.
     * <p>
     * Returns a new array if at least one argument was resolved. If not, might return the given
     * arguments (unmodified). DOES NEVER modify the given arguments array.
     */
    Object[] resolve(Resources2 resources2, @Nullable Locale locale, NotFoundConfig notFoundConfig, Object[] args);

    /**
     * Resolves the given arguments.
     * <p>
     * Returns a new array if at least one argument was resolved. If not, might return the given
     * arguments (unmodified). DOES NEVER modify the given arguments array. Returns <code>null</code> if at least
     * one argument could not be resolved (resource not found).
     */
    @Nullable
    Object[] maybeResolve(Resources2 resources2, @Nullable Locale locale, Object[] args);
}
