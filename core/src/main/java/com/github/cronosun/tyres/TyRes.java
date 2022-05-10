package com.github.cronosun.tyres;

import com.github.cronosun.tyres.implementation.DefaultImplementation;

import java.lang.reflect.Method;

public final class TyRes {

    private static final String INSTANCE_METHOD_NAME = "instance";

    private TyRes() {
    }

    public static <T> T create(Class<T> bundleClass) {
        return implementation(bundleClass).createInstance(bundleClass);
    }

    private static TyResImplementation implementation(Class<?> bundleClass) {
        var annotation = bundleClass.getAnnotation(UseImplementation.class);
        if (annotation==null) {
            return DefaultImplementation.instance();
        } else {
            return implementationFromAnnotation(annotation);
        }
    }

    private static TyResImplementation implementationFromAnnotation(UseImplementation useImplementation) {
        var implementationClass = useImplementation.value();
        try {
            var instanceMethod = implementationClass.getDeclaredMethod(INSTANCE_METHOD_NAME);
            return implementationFromMethod(instanceMethod);
        } catch (NoSuchMethodException e) {
            throw new TyResException("Method '" + INSTANCE_METHOD_NAME + "' not found. Add static method " +
                    "to class " + implementationClass + "; no arguments, return type must be of type "+
                    TyResImplementation.class + "; not inherited.");
        }
    }

    private static TyResImplementation implementationFromMethod(Method method) {
        var returnType = method.getReturnType();
        if (!TyResImplementation.class.isAssignableFrom(returnType)) {
            throw new TyResException("Method '" + INSTANCE_METHOD_NAME + "' of class " +
                    method.getDeclaringClass() + " must return a type that's assignable to " +
                    TyResImplementation.class + "; given type " + returnType + " is not.");
        }
        try {
            return (TyResImplementation) method.invoke(null);
        } catch (Exception exception) {
            throw new TyResException("Got exception invoking static method '" + INSTANCE_METHOD_NAME +
                    "' of class " + method.getDeclaringClass() + ".", exception);
        }
    }
}
