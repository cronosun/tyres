package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public abstract class ResInfo implements WithConciseDebugString {

  private final BundleInfo bundle;
  private final Method method;

  private ResInfo(BundleInfo bundle, Method method) {
    this.bundle = bundle;
    this.method = method;
  }

  static Res<?> reflect(BundleInfo bundleInfo, Method method) {
    return ResInfoReflection.reflect(bundleInfo, method);
  }

  /**
   * Information about the bundle.
   */
  public final BundleInfo bundle() {
    return bundle;
  }

  /**
   * The method that has been used to get those {@link ResInfo} from.
   */
  public final Method method() {
    return method;
  }

  @Override
  public String toString() {
    return "ResInfo{" + "bundle=" + bundle + ", method=" + method + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResInfo resInfo = (ResInfo) o;
    return bundle.equals(resInfo.bundle) && method.equals(resInfo.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundle, method);
  }

  /**
   * {@link ResInfo} for string and message resouces.
   */
  public static final class Str extends ResInfo {

    private final String name;

    @Nullable
    private final String defaultValue;

    public Str(BundleInfo bundle, Method method, String name, @Nullable String defaultValue) {
      super(bundle, method);
      this.name = name;
      this.defaultValue = defaultValue;
    }

    /**
     * The name. This is either {@link Method#getName()} or the name from the {@link Rename}-annotation (if
     * this annotation is present).
     */
    public String name() {
      return name;
    }

    /**
     * The default value from the {@link Default}-annotation or null if there's no such annotation.
     * <p>
     * Note: The default value is not the same as the fallback value. The default value is a "normal" value that
     * is considered to be OK. This default value can be used instead of writing a message bundle for the default
     * locale.
     * <p>
     * Example with default values in the bundle:
     * <pre>
     *     messages.properties    -> values for the default locale
     *     messages_de.properties -> german translation
     * </pre>
     * <p>
     * Instead of doing that, you can use the {@link Default}-annotation instead of <code>messages.properties</code>.
     */
    @Nullable
    public String defaultValue() {
      return defaultValue;
    }

    @Override
    public String conciseDebugString() {
      if (defaultValue != null) {
        return WithConciseDebugString.build(List.of(bundle(), name, defaultValue));
      } else {
        return WithConciseDebugString.build(List.of(bundle(), name));
      }
    }

    @Override
    public String toString() {
      return (
        "Str{" +
        "bundle=" +
        bundle() +
        ", method=" +
        method() +
        ", name='" +
        name +
        '\'' +
        ", defaultValue='" +
        defaultValue +
        '\'' +
        '}'
      );
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Str str = (Str) o;
      return name.equals(str.name) && Objects.equals(defaultValue, str.defaultValue);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), name, defaultValue);
    }
  }

  public static final class Bin extends ResInfo {

    private final Filename filename;

    public Bin(BundleInfo bundleInfo, Method method, Filename filename) {
      super(bundleInfo, method);
      this.filename = filename;
    }

    public Filename filename() {
      return filename;
    }

    @Override
    public String conciseDebugString() {
      return WithConciseDebugString.build(
        List.of(bundle(), "file", WithConciseDebugString.text(filename.value()))
      );
    }

    @Override
    public String toString() {
      return (
        "Bin{" + "bundle=" + bundle() + ", method=" + method() + ", filename=" + filename + '}'
      );
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      Bin bin = (Bin) o;
      return filename.equals(bin.filename);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), filename);
    }
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
      return new ResInfo.Str(bundleInfo, method, name, defaultValue);
    }

    private static ResInfo reflectFileResource(
      BundleInfo bundleInfo,
      Method method,
      File fileAnnotation
    ) {
      var filename = fileAnnotation.value();
      return new ResInfo.Bin(bundleInfo, method, Filename.from(filename));
    }
  }
}
