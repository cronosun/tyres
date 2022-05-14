package com.github.cronosun.tyres.core;

import com.github.cronosun.tyres.core.ReflectionInfo.ResReflectionInfo;
import com.github.cronosun.tyres.core.ReflectionInfo.ReturnValueConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Reflection implementation. Private, see {@link ReflectionInfo#getFrom(Class, TyResImplementation)}.
 */
final class Reflection {

    private static final String CREATE_METHOD_NAME = "create";
    private static final String WITH_ARGS_METHOD_NAME = "withArgs";
    private static final Class<?> OBJECT_ARRAY_CLASS = Object[].class;

    private Reflection() {
    }

    public static ReflectionInfo reflect(TyResImplementation implementation, Class<?> bundleClass) {
        var bundleInfo = reflectBundle(implementation, bundleClass);
        // note: we also include inherited methods.
        var methods = bundleClass.getMethods();
        var resources = resReflectionInfoFromMethods(bundleInfo, methods);
        return new DefaultReflectionInfo(resources, bundleInfo);
    }

    private static Collection<ResReflectionInfo> resReflectionInfoFromMethods(
            DefaultBundleInfo bundle,
            Method[] methods
    ) {
        var stream = Arrays
                .stream(methods)
                .<ResReflectionInfo>map(method -> {
                    var resInfo = reflectMethod(bundle, method);
                    final ReturnValueConstructor returnValueConstructor = createDefaultReturnTypeConstructor(
                            bundle,
                            resInfo
                    );
                    return new DefaultResReflectionInfo(resInfo, returnValueConstructor);
                });
        return stream.collect(Collectors.toUnmodifiableList());
    }

    private static ResInfo reflectMethod(DefaultBundleInfo bundle, Method method) {
        var name = getNameFrom(method);
        var defaultValue = getDefaultValueFrom(method);
        return new DefaultResInfo(bundle, method, name, defaultValue);
    }

    private static DefaultBundleInfo reflectBundle(
            TyResImplementation implementation,
            Class<?> bundleClass
    ) {
        var baseName = getBaseName(bundleClass);
        return new DefaultBundleInfo(bundleClass, baseName, implementation);
    }

    private static DefaultReturnTypeConstructor createDefaultReturnTypeConstructor(
            BundleInfo bundleInfo,
            ResInfo resInfo
    ) {
        var method = resInfo.method();
        // make sure the return type is of correct type
        assertNewResIsAssignableFromReturnType(method);
        return createReturnTypeCreator(method);
    }

    private static void assertNewResIsAssignableFromReturnType(Method method) {
        var returnType = method.getReturnType();
        if (!Res.class.isAssignableFrom(returnType)) {
            var methodName = method.getName();
            var declaringClass = method.getDeclaringClass();
            throw new TyResException(
                    "Invalid return type for method '" +
                            methodName +
                            "' (declaring class '" +
                            declaringClass.getName() +
                            "'). Return type must be " +
                            "of a class that implements " +
                            Res.class.getSimpleName() +
                            ". Got '" +
                            returnType.getName() +
                            "'."
            );
        }
    }

    private static DefaultReturnTypeConstructor createReturnTypeCreator(Method method) {
        var returnType = method.getReturnType();
        final Method createMethod;
        try {
            createMethod =
                    returnType.getDeclaredMethod(CREATE_METHOD_NAME, BundleInfo.class, ResInfo.class);
        } catch (Exception exception) {
            throw new TyResException(
                    "Class '" +
                            returnType +
                            "' does not implement static method called " +
                            CREATE_METHOD_NAME +
                            " (arguments " +
                            BundleInfo.class.getSimpleName() +
                            ", " +
                            ResInfo.class.getSimpleName() +
                            ") - or it's not accesible by reflection.",
                    exception
            );
        }
        var createMethodReturnType = createMethod.getReturnType();
        if (!returnType.isAssignableFrom(createMethodReturnType)) {
            throw new TyResException(
                    "Method " +
                            CREATE_METHOD_NAME +
                            " in '" +
                            returnType +
                            "'' must return a type where '" +
                            returnType +
                            "' is assignable from; It's not assignable from the given type '" +
                            createMethodReturnType +
                            "'."
            );
        }
        // also make sure that the 'withArgs' method is correct
        final Method withArgsMethod;
        try {
            withArgsMethod = returnType.getDeclaredMethod(WITH_ARGS_METHOD_NAME, OBJECT_ARRAY_CLASS);
        } catch (Exception exception) {
            throw new TyResException(
                    "Class '" +
                            returnType +
                            "' has missing or inaccessible method '" +
                            WITH_ARGS_METHOD_NAME +
                            "'. Note: if the class '" +
                            returnType.getSimpleName() +
                            "' is abstract, it must declare this method as abstract with the correct return" +
                            " type (inherited methods from the interface are not included in this check).",
                    exception
            );
        }
        var withArgsMethodReturnType = withArgsMethod.getReturnType();
        if (!withArgsMethodReturnType.isAssignableFrom(withArgsMethodReturnType)) {
            throw new TyResException(
                    "Method " +
                            WITH_ARGS_METHOD_NAME +
                            " in '" +
                            returnType +
                            "'' must return a type where '" +
                            returnType +
                            "' is assignable from; It's not assignable from the given type '" +
                            withArgsMethodReturnType +
                            "'."
            );
        }
        // everything is ok
        return new DefaultReturnTypeConstructor(createMethod);
    }

    private static String getNameFrom(Method method) {
        var maybeAnnotation = method.getAnnotation(RenameMethod.class);
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

    private static BaseName getBaseName(Class<?> bundleClass) {
        var renamePackageAnnotation = bundleClass.getAnnotation(RenamePackage.class);
        var renameNameAnnotation = bundleClass.getAnnotation(RenameName.class);

        if (renamePackageAnnotation == null && renameNameAnnotation == null) {
            // 99% case
            return BaseName.fromClass(bundleClass);
        }

        final String packageName;
        if (renamePackageAnnotation == null) {
            packageName = bundleClass.getPackageName();
        } else {
            packageName = renamePackageAnnotation.value();
        }

        final String name;
        if (renameNameAnnotation == null) {
            name = bundleClass.getSimpleName();
        } else {
            name = renameNameAnnotation.value();
        }

        return BaseName.fromPackageAndName(packageName, name);
    }

    private static final class DefaultReflectionInfo implements ReflectionInfo {

        private final DefaultBundleInfo bundleInfo;
        private final Collection<ResReflectionInfo> resources;

        private DefaultReflectionInfo(
                Collection<ResReflectionInfo> resources,
                DefaultBundleInfo bundleInfo
        ) {
            this.resources = resources;
            this.bundleInfo = bundleInfo;
        }

        @Override
        public BundleInfo bundleInfo() {
            return bundleInfo;
        }

        @Override
        public Collection<ResReflectionInfo> resources() {
            return resources;
        }
    }

    private static final class DefaultResReflectionInfo implements ResReflectionInfo {

        private final ResInfo resInfo;

        public DefaultResReflectionInfo(
                ResInfo resInfo,
                ReturnValueConstructor returnValueConstructor
        ) {
            this.resInfo = resInfo;
            this.returnValueConstructor = returnValueConstructor;
        }

        private final ReturnValueConstructor returnValueConstructor;

        @Override
        public ResInfo resInfo() {
            return resInfo;
        }

        @Override
        public ReturnValueConstructor returnValueConstructor() {
            return returnValueConstructor;
        }
    }

    private static final class DefaultBundleInfo implements BundleInfo {

        private final Class<?> bundleClass;
        private final BaseName baseName;
        private final TyResImplementation implementation;

        private DefaultBundleInfo(
                Class<?> bundleClass,
                BaseName baseName,
                TyResImplementation implementation
        ) {
            this.bundleClass = bundleClass;
            this.baseName = baseName;
            this.implementation = implementation;
        }

        @Override
        public Class<?> bundleClass() {
            return bundleClass;
        }

        @Override
        public BaseName baseName() {
            return baseName;
        }

        @Override
        public TyResImplementation implementation() {
            return implementation;
        }

        @Override
        public String toString() {
            return "DefaultBundleInfo [bundleClass=" + bundleClass + ", baseName=" + baseName + ", implementation="
                    + implementation + "]";
        }
    }

    private static final class DefaultResInfo implements ResInfo {

        private final DefaultBundleInfo bundleInfo;
        private final Method method;
        private final String name;

        @Nullable
        private final String defaultValue;

        private DefaultResInfo(
                DefaultBundleInfo bundleInfo,
                Method method,
                String name,
                @Nullable String defaultValue
        ) {
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

        @Override
        public String toString() {
            return "DefaultResInfo [bundleInfo=" + bundleInfo + ", defaultValue=" + defaultValue + ", method=" + method
                    + ", name=" + name + "]";
        }
    }

    private static final class DefaultReturnTypeConstructor implements ReturnValueConstructor {

        public DefaultReturnTypeConstructor(Method createMethod) {
            this.createMethod = createMethod;
        }

        private final Method createMethod;

        @Override
        public Res<?> construct(BundleInfo bundleInfo, ResInfo resInfo) {
            try {
                return (Res<?>) this.createMethod.invoke(null, bundleInfo, resInfo);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new TyResException(
                        "Unable to invoke the '" +
                                CREATE_METHOD_NAME +
                                "' on the return type. Method is '" +
                                createMethod +
                                "'.",
                        e
                );
            }
        }
    }
}
