package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Reflection implementation. Private, see {@link ReflectionInfo#getFrom(Class, TyResImplementation)}.
 */
final class Reflection {

  private Reflection() {}

  public static ReflectionInfo reflect(TyResImplementation implementation, Class<?> bundleClass) {
    var bundleInfo = BundleInfoReflection.reflect(bundleClass, implementation);
    // note: we also include inherited methods.
    var methods = bundleClass.getMethods();
    var resources = Arrays
      .stream(methods)
      .map(method -> ResInfoReflection.reflect(bundleInfo, method))
      .collect(Collectors.toUnmodifiableList());
    return new DefaultReflectionInfo(resources, bundleInfo);
  }

  private static final class DefaultReflectionInfo implements ReflectionInfo {

    private final BundleInfo bundleInfo;
    private final Collection<ResInfo> resources;

    private DefaultReflectionInfo(Collection<ResInfo> resources, BundleInfo bundleInfo) {
      this.resources = resources;
      this.bundleInfo = bundleInfo;
    }

    @Override
    public BundleInfo bundleInfo() {
      return bundleInfo;
    }

    @Override
    public Collection<ResInfo> resources() {
      return resources;
    }
  }
}
