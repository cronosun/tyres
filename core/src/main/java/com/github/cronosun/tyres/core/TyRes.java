package com.github.cronosun.tyres.core;

import java.util.Objects;
import java.util.ServiceLoader;
import org.jetbrains.annotations.Nullable;

public final class TyRes {

  private static final Object SYNCHRONIZATION_LOCK = new Object();

  @Nullable
  private static volatile TyResImplementation IMPLEMETATION;

  private TyRes() {}

  public static <T> T create(Class<T> bundleClass) {
    var implementation = implementation();
    return implementation.createInstance(bundleClass);
  }

  private static TyResImplementation implementation() {
    var implementation = IMPLEMETATION;
    if (implementation != null) {
      return implementation;
    } else {
      synchronized (SYNCHRONIZATION_LOCK) {
        var implementationSecondTry = IMPLEMETATION;
        if (implementationSecondTry != null) {
          return implementationSecondTry;
        }
        var determinedImplementation = determineImplementation();
        Objects.requireNonNull(determinedImplementation);
        IMPLEMETATION = determinedImplementation;
        return determinedImplementation;
      }
    }
  }

  private static TyResImplementation determineImplementation() {
    var serviceLoader = ServiceLoader.load(TyResImplementation.class);
    var iterator = serviceLoader.iterator();
    if (iterator.hasNext()) {
      var implementation = iterator.next();
      if (iterator.hasNext()) {
        var secondImplementation = iterator.next();
        throw new TyResException(
          "There are at least two implementations of TyRes on the classpath. To " +
          "make sure the application produces reproducible results, only one implementation is " +
          "supported. Found implementations (might have more): [" +
          implementation.getClass() +
          ", " +
          secondImplementation.getClass() +
          "]."
        );
      }
      return implementation;
    } else {
      throw new TyResException(
        "No tyres implementation found. Make sure exactly one implementation of " +
        "TyRes is on the classpath (loadable by java ServiceLoader)."
      );
    }
  }
}
