package com.github.cronosun.tyres.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.stream.Collectors;

final class DefaultImplementation implements TyResImplementation {

  private static final TyResImplementation INSTANCE = new DefaultImplementation();

  public static TyResImplementation instance() {
    return INSTANCE;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T createInstance(Class<T> bundleClass) {
    //noinspection unchecked
    return (T) Proxy.newProxyInstance(
      bundleClass.getClassLoader(),
      new Class[] { bundleClass },
      new TyResInvocationHandler(bundleClass)
    );
  }

  @Override
  public ReflectionInfo reflectionInfo(Object instance) {
    var invocationHandler = Proxy.getInvocationHandler(instance);
    if (invocationHandler instanceof TyResInvocationHandler) {
      var handler = (TyResInvocationHandler) invocationHandler;
      return handler.bundleResInfo();
    } else {
      throw new TyResException(
        "The given instance " +
        instance +
        " seems to be something that has not been created by this instance (read the docs!)."
      );
    }
  }

  @Override
  public String toString() {
    return "DefaultImplementation";
  }

  static final class TyResInvocationHandler implements InvocationHandler {

    private final Map<Method, ? extends Res<?>> map;
    private final ReflectionInfo bundleResInfo;

    public TyResInvocationHandler(Class<?> bundleClass) {
      var bundleResInfo = ReflectionInfo.getFrom(DefaultImplementation.instance(), bundleClass);
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
}
