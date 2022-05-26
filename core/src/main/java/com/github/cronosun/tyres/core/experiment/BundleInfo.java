package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.RenameName;
import com.github.cronosun.tyres.core.RenamePackage;
import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class BundleInfo implements WithConciseDebugString {

  private final Class<?> bundleClass;
  private final BaseName baseName;

  private BundleInfo(Class<?> bundleClass, BaseName baseName) {
    this.bundleClass = bundleClass;
    this.baseName = baseName;
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

  public Stream<MethodInfo> unvalidatedMethods() {
    return Arrays.stream(bundleClass.getMethods()).map(MethodInfo::reflect);
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
    return bundleClass.equals(that.bundleClass) && baseName.equals(that.baseName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleClass, baseName);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(List.of(baseName()));
  }
}
