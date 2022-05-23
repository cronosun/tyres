package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Information about all resources in the bundle and information about the bundle
 * itself.
 */
@ThreadSafe
public final class ReflectionInfo {

  private final BundleInfo bundleInfo;
  private final Collection<Res<?>> resources;

  public ReflectionInfo(BundleInfo bundleInfo, Stream<Res<?>> resources) {
    this.bundleInfo = bundleInfo;
    this.resources = resources.collect(Collectors.toUnmodifiableList());
  }

  /**
   * Default reflection implementation that conforms to the specification and can be used by
   * implementations (unless they want to provide their own implementation).
   */
  public static ReflectionInfo getFrom(TyResImplementation implementation, Class<?> bundleClass) {
    return Reflection.reflect(implementation, bundleClass);
  }

  @Override
  public String toString() {
    return "ReflectionInfo{" + "bundleInfo=" + bundleInfo + ", resources=" + resources + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReflectionInfo that = (ReflectionInfo) o;
    return bundleInfo.equals(that.bundleInfo) && resources.equals(that.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleInfo, resources);
  }

  public BundleInfo bundleInfo() {
    return this.bundleInfo;
  }

  /**
   * Returns all declared resources.
   * <p>
   * Expect the collection to be immutable.
   */
  public Collection<Res<?>> resources() {
    return this.resources;
  }

  private static final class Reflection {

    private Reflection() {}

    public static ReflectionInfo reflect(
      TyResImplementation implementation,
      Class<?> bundleClass
    ) {
      var bundleInfo = BundleInfo.reflect(bundleClass, implementation);
      // note: we also include inherited methods.
      var methods = bundleClass.getMethods();
      final Stream<Res<?>> streamOfResources = Arrays
        .stream(methods)
        .map(method -> ResInfo.reflect(bundleInfo, method));
      return new ReflectionInfo(bundleInfo, streamOfResources);
    }
  }
}
