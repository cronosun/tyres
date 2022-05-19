package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BundleResInfo;
import com.github.cronosun.tyres.core.TyResException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class TyResInvocationHandler implements InvocationHandler {

    private final Map<Method, ResNoArgs<?>> map;

    public TyResInvocationHandler(Class<?> bundleClass) {
        var bundleResInfo = BundleResInfo.getFrom(bundleClass, DefaultImplementation.instance());
        var length = bundleResInfo.resources().size();
        var map = new HashMap<Method, ResNoArgs<?>>(length);
        for (var resInfo : bundleResInfo.resources()) {
            var key = resInfo.method();
            var value = new ResNoArgs<>(resInfo);
            map.put(key, value);
        }
       this.map = map;
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
