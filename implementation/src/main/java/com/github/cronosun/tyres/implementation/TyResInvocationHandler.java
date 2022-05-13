package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BundleResInfo;
import com.github.cronosun.tyres.core.TyResException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TyResInvocationHandler implements InvocationHandler {

    private static final Logger LOG = Logger.getLogger(TyResInvocationHandler.class.getName());
    private final TrMap<Method, ResNoArgs<?>> map;

    public TyResInvocationHandler(Class<?> bundleClass) {
        var bundleResInfo = BundleResInfo.getFrom(bundleClass);
        var length = bundleResInfo.resources().size();
        var capacity = capacity(length);
        var stream = bundleResInfo.resources().stream().map(resource -> {
            var key = resource.method();
            var value = new ResNoArgs<>(resource);
            return new TrMap.Entry<Method, ResNoArgs<?>>(key, value);
        });
       var map =TrMap.createInstance(HashFunctionImpl.INSTANCE,capacity, stream);
       logConflicts(map);
       this.map = map;
    }

    private static int capacity(int length) {
        // add 1/3
        return length + (length / 3);
    }

    private static void logConflicts(TrMap<Method, ResNoArgs<?>> map) {
        LOG.log(Level.WARNING, "Conflicts " + map.numberOfConflicts() + "; length " + map.length());
        // TODO: Java default logger... if there are too many conflicts
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        var value = map.get(method);
        if (value==null) {
            throw new TyResException("Method not found using reflection (or implementation error). Method: " + method);
        }
        if (args==null || args.length==0) {
            return value;
        } else {
            return new ResWithArgs<>(value.info(), args);
        }
    }
}
