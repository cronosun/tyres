package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@ThreadSafe
public final class BundleInfo implements WithConciseDebugString {

  private final Class<?> bundleClass;
  private final BaseName baseName;

  @Nullable
  private final Object implementationData;

  public BundleInfo(Class<?> bundleClass, BaseName baseName) {
    this(bundleClass, baseName, null);
  }

  public BundleInfo(Class<?> bundleClass, BaseName baseName, @Nullable Object implementationData) {
    this.bundleClass = Objects.requireNonNull(bundleClass);
    this.baseName = Objects.requireNonNull(baseName);
    this.implementationData = implementationData;
  }

  public static BundleInfo reflect(
    Class<?> bundleClass,
    ImplementationDataProvider implementationDataProvider
  ) {
    var bundleInfo = new BundleInfo(bundleClass, getBaseName(bundleClass));
    var implementationData = implementationDataProvider.implementationDataForBundle(bundleInfo);
    if (implementationData != null) {
      return new BundleInfo(bundleInfo.bundleClass(), bundleInfo.baseName(), implementationData);
    } else {
      return bundleInfo;
    }
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

  public Class<?> bundleClass() {
    return bundleClass;
  }

  public BaseName baseName() {
    return baseName;
  }

  /**
   * This is additional data the implementation can add (optionally). This is implementation specific and should not be
   * used, unless you write a {@link Resources} implementation.
   */
  @Nullable
  public Object implementationData() {
    return this.implementationData;
  }

  public Stream<EntryInfo> resources(ImplementationDataProvider implementationDataProvider) {
    return Arrays
      .stream(bundleClass.getMethods())
      .map(method -> EntryInfo.reflect(this, method, implementationDataProvider));
  }

  public int numberOfMethods() {
    return bundleClass.getMethods().length;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BundleInfo that = (BundleInfo) o;
    return (
      bundleClass.equals(that.bundleClass) &&
      baseName.equals(that.baseName) &&
      Objects.equals(implementationData, that.implementationData)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleClass, baseName, implementationData);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(List.of(baseName()));
  }

  @Override
  public String toString() {
    return (
      "BundleInfo{" +
      "bundleClass=" +
      bundleClass +
      ", baseName=" +
      baseName +
      ", implementationData=" +
      implementationData +
      '}'
    );
  }
}
