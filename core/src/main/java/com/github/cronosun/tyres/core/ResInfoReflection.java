package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;

final class ResInfoReflection {

  private ResInfoReflection() {}

  public static ResInfo reflect(BundleInfo bundleInfo, Method method) {
    checkReturnType(method);
    var fileAnnotation = method.getAnnotation(File.class);
    var renameAnnotation = method.getAnnotation(Rename.class);
    var defaultAnnotation = method.getAnnotation(Default.class);
    if (fileAnnotation != null) {
      // it's a file
      if (renameAnnotation != null) {
        throw new TyResException(
          "Method '" +
          method +
          "' has the " +
          File.class.getSimpleName() +
          " and the " +
          Rename.class.getSimpleName() +
          " annotations; cannot have both (one is for string resources and " +
          "the other one for file resources)."
        );
      }
      if (defaultAnnotation != null) {
        throw new TyResException(
          "Method '" +
          method +
          "' has the " +
          File.class.getSimpleName() +
          " and the " +
          Default.class.getSimpleName() +
          " annotations; cannot have both (one is for string resources and " +
          "the other one for file resources)."
        );
      }
      return reflectFileResource(bundleInfo, method, fileAnnotation);
    } else {
      // it's a string resource
      return reflectStringResouce(bundleInfo, method, renameAnnotation, defaultAnnotation);
    }
  }

  private static void checkReturnType(Method method) {
    var returnType = method.getReturnType();
    if (!returnType.isAssignableFrom(Res.class)) {
      throw new TyResException(
        "Method '" +
        method +
        "' return type is '" +
        returnType.getSimpleName() +
        "'. Methods must return '" +
        Res.class.getSimpleName() +
        "' or something that's assignable from '" +
        Res.class.getSimpleName() +
        "'."
      );
    }
  }

  private static ResInfo reflectStringResouce(
    BundleInfo bundleInfo,
    Method method,
    @Nullable Rename renameAnnotation,
    @Nullable Default defaultAnnotation
  ) {
    final String name;
    if (renameAnnotation != null) {
      name = renameAnnotation.value();
    } else {
      name = method.getName();
    }
    final String defaultValue;
    if (defaultAnnotation != null) {
      defaultValue = defaultAnnotation.value();
    } else {
      defaultValue = null;
    }
    var details = new ResInfoDetails.StringResource(name, defaultValue);
    return new ResInfo(bundleInfo, method, details);
  }

  private static ResInfo reflectFileResource(
    BundleInfo bundleInfo,
    Method method,
    File fileAnnotation
  ) {
    var filename = fileAnnotation.filename();
    var details = new ResInfoDetails.FileResouce(filename);
    return new ResInfo(bundleInfo, method, details);
  }
}
