package com.github.cronosun.tyres.core.implementation;

import com.github.cronosun.tyres.core.ReflectionInfo;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.TyResImplementation;

import java.lang.reflect.Proxy;

public final class DefaultImplementation implements TyResImplementation {

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
  public ReflectionInfo bundleResInfo(Object instance) {
    var invicationHandler = Proxy.getInvocationHandler(instance);
    if (invicationHandler instanceof TyResInvocationHandler) {
      var handler = (TyResInvocationHandler) invicationHandler;
      return handler.bundleResInfo();
    } else {
      throw new TyResException(
        "The given instance " +
        instance +
        " seems to be something that has not been created by this instance (read the docs!)."
      );
    }
  }
}
