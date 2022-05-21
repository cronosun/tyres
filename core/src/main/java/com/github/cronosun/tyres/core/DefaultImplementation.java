package com.github.cronosun.tyres.core;

import java.lang.reflect.Proxy;

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
}
