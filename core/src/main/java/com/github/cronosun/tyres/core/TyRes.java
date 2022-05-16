package com.github.cronosun.tyres.core;

import java.util.Objects;
import java.util.ServiceLoader;

import com.github.cronosun.tyres.core.implementation.DefaultImplementation;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class TyRes {

  private static final Object SYNCHRONIZATION_LOCK = new Object();

  @Nullable
  private static volatile TyResImplementation IMPLEMENTATION;

  private TyRes() {}

  /**
   * Creates a bundle from the given bundle class.
   */
  @ThreadSafe
  public static <T> T create(Class<T> bundleClass) {
    var implementation = implementation();
    return implementation.createInstance(bundleClass);
  }

  private static TyResImplementation implementation() {
    var implementation = IMPLEMENTATION;
    if (implementation != null) {
      return implementation;
    } else {
      synchronized (SYNCHRONIZATION_LOCK) {
        var implementationSecondTry = IMPLEMENTATION;
        if (implementationSecondTry != null) {
          return implementationSecondTry;
        }
        var determinedImplementation = determineImplementation();
        Objects.requireNonNull(determinedImplementation);
        IMPLEMENTATION = determinedImplementation;
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
      // return the default implementation
      return DefaultImplementation.instance();
    }
  }
}
