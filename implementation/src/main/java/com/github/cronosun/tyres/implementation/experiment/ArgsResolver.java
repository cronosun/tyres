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
     * Note 1: DOES NEVER modify the given arguments array. Returns a new array if at least one argument was resolved.
     * Note 2: The method is allowed to return the given args-array (if there are no arguments to resolve).
     * Note 3: Returns <code>null</code> if `notFoundConfig` is {@link NotFoundConfig.WithNullNoDefault#NULL} and at least one
     * argument could not be resolved.
     */
    @Nullable
    Object[] resolve(Resources2 resources, Locale locale, NotFoundConfig.WithNullNoDefault notFoundConfig, Object[] args);
}
