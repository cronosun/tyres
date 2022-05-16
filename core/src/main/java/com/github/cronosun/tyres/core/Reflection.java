package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reflection implementation. Private, see {@link ReflectionInfo#getFrom(Class, TyResImplementation)}.
 */
final class Reflection {

  private Reflection() {}

  public static ReflectionInfo reflect(TyResImplementation implementation, Class<?> bundleClass) {
    var bundleInfo = BundleInfoReflection.reflect(bundleClass, implementation);
    // note: we also include inherited methods.
    var methods = bundleClass.getMethods();
    final Stream<Res<?>> stream = Arrays
      .stream(methods)
      .map(method -> ResInfoReflection.reflect(bundleInfo, method));
    var resources = stream.collect(Collectors.toUnmodifiableList());
    return new DefaultReflectionInfo(resources, bundleInfo);
  }

  private static final class DefaultReflectionInfo implements ReflectionInfo {

    private final BundleInfo bundleInfo;
    private final Collection<Res<?>> resources;

    private DefaultReflectionInfo(Collection<Res<?>> resources, BundleInfo bundleInfo) {
      this.resources = resources;
      this.bundleInfo = bundleInfo;
    }

    @Override
    public BundleInfo bundleInfo() {
      return bundleInfo;
    }

    @Override
    public Collection<Res<?>> resources() {
      return resources;
    }
  }
}
