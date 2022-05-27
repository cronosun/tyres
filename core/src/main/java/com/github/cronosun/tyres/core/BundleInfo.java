package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@ThreadSafe
public final class BundleInfo implements WithConciseDebugString {

  private final Class<?> bundleClass;
  private final BaseName baseName;
  private final BaseName effectiveBaseName;

  public BundleInfo(Class<?> bundleClass, BaseName baseName) {
    this(bundleClass, baseName, baseName);
  }

  public BundleInfo(Class<?> bundleClass, BaseName baseName, BaseName effectiveBaseName) {
    this.bundleClass = Objects.requireNonNull(bundleClass);
    this.baseName = Objects.requireNonNull(baseName);
    this.effectiveBaseName = Objects.requireNonNull(effectiveBaseName);
  }

  public static BundleInfo reflect(Class<?> bundleClass) {
    return new BundleInfo(bundleClass, getBaseName(bundleClass));
  }

  public Class<?> bundleClass() {
    return bundleClass;
  }

  public BaseName baseName() {
    return baseName;
  }

  /**
   * The base name that is used for lookup in the bundle. It's usually (and initially) the same as
   * {@link #baseName()} - but the implementation is allowed to change that.
   */
  public BaseName effectiveBaseName() {
    return this.effectiveBaseName;
  }

  public Stream<ResInfo> resources(ResInfo.EffectiveNameGenerator effectiveNameGenerator) {
    return Arrays
      .stream(bundleClass.getMethods())
      .map(method -> ResInfo.reflect(this, method, effectiveNameGenerator));
  }

  public int numberOfMethods() {
    return bundleClass.getMethods().length;
  }

  private static BaseName getBaseName(Class<?> bundleClass) {
    var renamePackageAnnotation = bundleClass.getAnnotation(RenamePackage.class);
    var renameNameAnnotation = bundleClass.getAnnotation(RenameName.class);

    if (renamePackageAnnotation == null && renameNameAnnotation == null) {
      // 99% case
      return BaseName.fromClass(bundleClass);
    }

    final String packageName;
    if (renamePackageAnnotation == null) {
      packageName = bundleClass.getPackageName();
    } else {
      packageName = renamePackageAnnotation.value();
    }

    final String name;
    if (renameNameAnnotation == null) {
      name = bundleClass.getSimpleName();
    } else {
      name = renameNameAnnotation.value();
    }

    return BaseName.fromPackageAndName(packageName, name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BundleInfo that = (BundleInfo) o;
    return (
      bundleClass.equals(that.bundleClass) &&
      baseName.equals(that.baseName) &&
      effectiveBaseName.equals(that.effectiveBaseName)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleClass, baseName, effectiveBaseName);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(List.of(baseName()));
  }

  public BundleInfo withEffectiveBaseName(BaseName effectiveBaseName) {
    if (effectiveBaseName.equals(this.effectiveBaseName())) {
      return this;
    } else {
      return new BundleInfo(bundleClass(), baseName(), effectiveBaseName);
    }
  }

  @Override
  public String toString() {
    return (
      "BundleInfo{" +
      "bundleClass=" +
      bundleClass +
      ", baseName=" +
      baseName +
      ", effectiveBaseName=" +
      effectiveBaseName +
      '}'
    );
  }
}
