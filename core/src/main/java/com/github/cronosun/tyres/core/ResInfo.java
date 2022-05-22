package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class ResInfo implements WithConciseDebugString {

  private final BundleInfo bundleInfo;
  private final Method method;
  private final ResInfoDetails details;

  public ResInfo(BundleInfo bundleInfo, Method method, ResInfoDetails details) {
    this.bundleInfo = Objects.requireNonNull(bundleInfo);
    this.method = Objects.requireNonNull(method);
    this.details = Objects.requireNonNull(details);
  }

  /**
   * Information about the bundle.
   */
  public BundleInfo bundle() {
    return bundleInfo;
  }

  /**
   * The method that has been used to get those {@link ResInfo} from.
   */
  public Method method() {
    return method;
  }

  public ResInfoDetails details() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResInfo resInfo = (ResInfo) o;
    return (
      bundleInfo.equals(resInfo.bundleInfo) &&
      method.equals(resInfo.method) &&
      details.equals(resInfo.details)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleInfo, method, details);
  }

  @Override
  public String toString() {
    return ("ResInfo{" + details + ", " + method + ", " + bundleInfo + '}');
  }

  @Override
  public String conciseDebugString() {
    var baseName = bundle().baseName();
    var details = details();
    return ConciseDebugString
      .create()
      .start()
      .append(baseName)
      .child()
      .append(details)
      .end()
      .finish();
  }

  static Res<?> reflect(BundleInfo bundleInfo, Method method) {
    return ResInfoReflection.reflect(bundleInfo, method);
  }

  private static final class ResInfoReflection {

    private static final String CREATE_METHOD_NAME = "create";

    private ResInfoReflection() {}

    public static Res<?> reflect(BundleInfo bundleInfo, Method method) {
      var createMethod = getReturnTypeCreateMethod(method);
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
        return createReturnValue(
          createMethod,
          reflectFileResource(bundleInfo, method, fileAnnotation)
        );
      } else {
        // it's a string resource
        return createReturnValue(
          createMethod,
          reflectStringResouce(bundleInfo, method, renameAnnotation, defaultAnnotation)
        );
      }
    }

    private static Res<?> createReturnValue(Method createMethod, ResInfo resInfo) {
      try {
        var returnValue = createMethod.invoke(null, resInfo);
        if (!(returnValue instanceof Res)) {
          throw new TyResException(
            "Return value constructor '" +
            createMethod +
            "' returned incompatible value (" +
            returnValue +
            ") - must be of type '" +
            returnValue.getClass().getSimpleName() +
            "'."
          );
        }
        return (Res<?>) returnValue;
      } catch (Exception exception) {
        throw new TyResException(
          "Unable to create return value using method '" + createMethod + "'.",
          exception
        );
      }
    }

    private static Method getReturnTypeCreateMethod(Method method) {
      var returnType = method.getReturnType();
      if (!Res.class.isAssignableFrom(returnType)) {
        throw new TyResException(
          "Method '" +
          method +
          "' return type is '" +
          returnType.getSimpleName() +
          "'. Methods must return something that implements '" +
          Res.class.getSimpleName() +
          "'."
        );
      }
      try {
        return returnType.getDeclaredMethod(CREATE_METHOD_NAME, ResInfo.class);
      } catch (NoSuchMethodException e) {
        throw new TyResException(
          "Return type (" +
          returnType.getSimpleName() +
          ") of method '" +
          method +
          "' must provide a static method called '" +
          CREATE_METHOD_NAME +
          "' with correct arguments."
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
      var details = new ResInfoDetails.StrResource(name, defaultValue);
      return new ResInfo(bundleInfo, method, details);
    }

    private static ResInfo reflectFileResource(
      BundleInfo bundleInfo,
      Method method,
      File fileAnnotation
    ) {
      var filename = fileAnnotation.value();
      var details = new ResInfoDetails.BinResource(filename);
      return new ResInfo(bundleInfo, method, details);
    }
  }
}
