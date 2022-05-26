package com.github.cronosun.tyres.core.experiment;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class DelegatingResources implements Resources2 {

    private final Resources2 resources;

    public DelegatingResources(Resources2 resources) {
        this.resources = resources;
    }

    @Override
    public final Text resolve(Resolvable resolvable) {
        return resources.resolve(resolvable);
    }

    @Override
    public final <T> T get(Class<T> bundleClass) {
        return resources.get(bundleClass);
    }

    @Override
    public final @Nullable Locale currentLocale() {
        return resources.currentLocale();
    }

    @Override
    public final DefaultNotFoundConfig defaultNotFoundConfig() {
        return resources.defaultNotFoundConfig();
    }
}
