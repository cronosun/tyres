package com.github.cronosun.tyres.implementation;

import javax.annotation.Nullable;
import java.util.List;

final class BaseInfo {
    private final Class<?> resourceClass;
    @Nullable
    private final List<String> customPackage;

    BaseInfo(Class<?> resourceClass, @Nullable List<String> customPackage) {
        this.resourceClass = resourceClass;
        this.customPackage = customPackage;
    }

    public List<String> getCustomPackage() {
        return customPackage;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }
}
