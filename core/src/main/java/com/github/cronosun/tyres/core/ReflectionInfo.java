package com.github.cronosun.tyres.core;

import java.util.Collection;

/**
 * Information about all resources in the bundle and information about the bundle
 * itself.
 */
public interface ReflectionInfo {
    /**
     * Default reflection implementation that conforms to the specification and can be used by
     * implementations (unless they want to provide their own implementation).
     */
    static ReflectionInfo getFrom(Class<?> bundleClass, TyResImplementation implementation) {
        return Reflection.reflect(implementation, bundleClass);
    }

    BundleInfo bundleInfo();

    Collection<ResReflectionInfo> resources();

    interface ResReflectionInfo {
        ResInfo resInfo();
        ReturnValueConstructor returnValueConstructor();
    }

    interface ReturnValueConstructor {
        NewRes<?> construct(BundleInfo bundleInfo, ResInfo resInfo);
    }
}
