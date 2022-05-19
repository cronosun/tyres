package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.ReflectionInfo;
import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.TyResException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class TyResInvocationHandler implements InvocationHandler {

  private final Map<Method, Res<?>> map;
  private final ReflectionInfo bundleResInfo;

  public TyResInvocationHandler(Class<?> bundleClass) {
    var bundleResInfo = ReflectionInfo.getFrom(bundleClass, DefaultImplementation.instance());
    var length = bundleResInfo.resources().size();
    var map = new HashMap<Method, Res<?>>(length);
    var bundleInfo = bundleResInfo.bundleInfo();
    for (var resource : bundleResInfo.resources()) {
      var resInfo = resource.resInfo();
      var key = resInfo.method();
      var value = resource.returnValueConstructor().construct(bundleInfo, resInfo);
      map.put(key, value);
    }
    this.map = map;
    this.bundleResInfo = bundleResInfo;
  }

  public ReflectionInfo bundleResInfo() {
    return bundleResInfo;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    var value = map.get(method);
    if (value == null) {
      throw new TyResException(
        "Method not found using reflection (or implementation error). Method: " + method
      );
    }
    if (args == null || args.length == 0) {
      return value;
    } else {
      return value.withArgs(args);
    }
  }
}
