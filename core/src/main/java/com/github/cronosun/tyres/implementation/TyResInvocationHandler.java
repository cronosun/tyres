package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.*;
import com.github.cronosun.tyres.Package;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

final class TyResInvocationHandler implements InvocationHandler {

    public TyResInvocationHandler(Class<?> resourceClass) {
        this.methods = loadMethods(resourceClass);
    }

    // TODO: We could speed that up: Actually we need something like this:
    // TODO: Map<Integer, SingleOrMultiple<ResNoArgs<?>>
    // TODO: Where the first integer ist just method.hashCode()
    // TODO: Since in 99.9% of the cases there won't be a conflict and we can be sure that it's the
    // TODO: correct method (since there can't be a method coming from somewhere else -> so we don't need to
    // TODO: compare the arguments).
    private final Map<Method, ResNoArgs<?>> methods;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        var maybeResInfo = methods.get(method);
        if (maybeResInfo!=null) {
            return invokeWithResInfo(maybeResInfo, args);
        } else {
            throw new TyResException("No resource info found for method '" + method +
                    "'. This is most likely a bug.");
        }
    }

    private Object invokeWithResInfo(ResNoArgs<?> resInfo, Object[] args) {
        if (args==null || args.length==0) {
            return resInfo;
        } else {
            return new ResWithArgs<>(resInfo, args);
        }
    }

    private static Map<Method, ResNoArgs<?>> loadMethods(Class<?> resourceClass) {
        var map = new HashMap<Method, ResNoArgs<?>>();
        var customPackage = customPackage(resourceClass);
        var baseInfo = new BaseInfo(resourceClass, customPackage);
        var methods = resourceClass.getMethods();
        for (var method : methods) {
            var methodIdentity = System.identityHashCode(method.hashCode());
            var resource = loadSingleMethod(method, baseInfo);
            map.put(method, resource);
        }
        return map;
    }

    @Nullable
    private static List<String> customPackage(Class<?> resouceClass) {
        var packageAnnotation = resouceClass.getAnnotation(Package.class);
        if (packageAnnotation==null) {
            return null;
        } else {
            var values = packageAnnotation.value();
            if (values!=null) {
                if (values.length==1) {
                    return List.of(values[0]);
                } else {
                    return Arrays.stream(values).collect(Collectors.toUnmodifiableList());
                }
            } else {
                throw new TyResException("Annotation " + Package.class + " MUST NOT have a null-value. See "
                        + resouceClass + ".");
            }
        }
    }

    private static ResNoArgs<?> loadSingleMethod(Method method, BaseInfo baseInfo) {
        assertReturnTypeIsValid(method);
        var name = getNameFrom(method);
        return new ResNoArgs<>(method, name, baseInfo);
    }

    private static String getNameFrom(Method method) {
        var maybeAnnotation = method.getAnnotation(Name.class);
        if (maybeAnnotation!=null) {
            var name = maybeAnnotation.value();
            if (name==null) {
                throw new TyResException("Name cannot be null. Method: " + method);
            }
            return name;
        } else {
            return method.getName();
        }
    }

    private static void assertReturnTypeIsValid(Method method) {
        var returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(Res.class)) {
            throw new TyResException("Invalid return type for method " + method + ". Return type must be " +
                    "either of class " + Res.class + " or of class " + Resource.class + ".");
        }
    }
}
