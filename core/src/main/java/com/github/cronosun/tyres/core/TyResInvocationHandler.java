package com.github.cronosun.tyres.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

final class TyResInvocationHandler implements InvocationHandler {

  private final Map<Method, ? extends Res<?>> map;
  private final ReflectionInfo bundleResInfo;

  public TyResInvocationHandler(Class<?> bundleClass) {
    var bundleResInfo = ReflectionInfo.getFrom(bundleClass, DefaultImplementation.instance());
    var resources = bundleResInfo.resources();

    this.map =
      resources
        .stream()
        .collect(
          Collectors.toUnmodifiableMap(item -> item.info().method(), item -> (Res<?>) item)
        );
    this.bundleResInfo = bundleResInfo;
  }

  public ReflectionInfo bundleResInfo() {
    return bundleResInfo;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    var value = map.get(method);
    if (value == null) {
      throw new TyResException("Method not found using reflection. Method: " + method);
    }
    if (args == null || args.length == 0) {
      return value;
    } else {
      return value.withArgs(args);
    }
  }
}