package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class Reflection {
    private Reflection() {
    }

    public static BundleResInfo reflect(Class<?> bundleClass) {
        var bundleInfo = reflectBundle(bundleClass);
        // note: we also include inherited methods.
        var methods = bundleClass.getMethods();
        return new DefaultBundleResInfo(methods, bundleInfo);
    }

    private static ResInfo reflectMethod(DefaultBundleInfo bundle, Method method) {
        assertReturnTypeIsValid(method);
        var name = getNameFrom(method);
        var defaultValue = getDefaultValueFrom(method);
        return new DefaultResInfo(bundle, method, name, defaultValue);
    }

    private static DefaultBundleInfo reflectBundle(Class<?> bundleClass) {
        var customPackage = customPackage(bundleClass);
        return new DefaultBundleInfo(bundleClass, customPackage);
    }

    private static void assertReturnTypeIsValid(Method method) {
        var returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(Res.class)) {
            var methodName = method.getName();
            var declaringClass = method.getDeclaringClass();
            throw new TyResException("Invalid return type for method '" + methodName + "' (declaring class '"+
                    declaringClass.getName()+"'). Return type must be " +
                    "either of class " + Res.class.getSimpleName() + " or of class " +
                    Resource.class.getSimpleName() + ". Got '" + returnType.getName() + "'.");
        }
    }

    private static String getNameFrom(Method method) {
        var maybeAnnotation = method.getAnnotation(Name.class);
        if (maybeAnnotation != null) {
            return maybeAnnotation.value();
        } else {
            return method.getName();
        }
    }

    @Nullable
    private static String getDefaultValueFrom(Method method) {
        var maybeAnnotation = method.getAnnotation(Default.class);
        if (maybeAnnotation != null) {
            return maybeAnnotation.value();
        } else {
            return null;
        }
    }

    @Nullable
    private static List<String> customPackage(Class<?> bundleClass) {
        var packageAnnotation = bundleClass.getAnnotation(Package.class);
        if (packageAnnotation == null) {
            return null;
        } else {
            var values = packageAnnotation.value();
            if (values != null) {
                if (values.length == 1) {
                    return List.of(values[0]);
                } else {
                    return Arrays.stream(values).collect(Collectors.toUnmodifiableList());
                }
            } else {
                throw new TyResException("Annotation " + Package.class + " MUST NOT have a null-value. See "
                        + bundleClass + ".");
            }
        }
    }

    private static final class DefaultBundleResInfo implements BundleResInfo {
        private final DefaultBundleInfo bundleInfo;
        private final Collection<ResInfo> resources;

        private DefaultBundleResInfo(Method[] methods, DefaultBundleInfo bundleInfo) {
            this.resources = Arrays.stream(methods).map(method -> reflectMethod(bundleInfo, method)).collect(Collectors.toUnmodifiableList());
            this.bundleInfo = bundleInfo;
        }

        @Override
        public BundleInfo bundleInfo() {
            return bundleInfo;
        }

        @Override
        public Collection<ResInfo> resources() {
            return resources;
        }
    }

    private static final class DefaultBundleInfo implements BundleInfo {
        private final Class<?> bundleClass;
        private final List<String> customPackage;

        private DefaultBundleInfo(Class<?> bundleClass, List<String> customPackage) {
            this.bundleClass = bundleClass;
            this.customPackage = customPackage;
        }

        @Override
        public Class<?> bundleClass() {
            return bundleClass;
        }

        @Nullable
        @Override
        public List<String> customPackage() {
            return customPackage;
        }
    }

    private static final class DefaultResInfo implements ResInfo {
        private final DefaultBundleInfo bundleInfo;
        private final Method method;
        private final String name;
        @Nullable
        private final String defaultValue;

        private DefaultResInfo(DefaultBundleInfo bundleInfo, Method method, String name, @Nullable String defaultValue) {
            this.bundleInfo = bundleInfo;
            this.method = method;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        @Override
        public BundleInfo bundle() {
            return bundleInfo;
        }

        @Override
        public Method method() {
            return method;
        }

        @Override
        public String name() {
            return name;
        }

        @Nullable
        @Override
        public String defaultValue() {
            return defaultValue;
        }
    }
}
