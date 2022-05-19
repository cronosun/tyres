package com.github.cronosun.tyres.core;

import com.github.cronosun.tyres.core.ReflectionInfo.ResReflectionInfo;
import com.github.cronosun.tyres.core.ReflectionInfo.ReturnValueConstructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

final class Reflection {

  private static final String CREATE_METHOD_NAME = "create";
  private static final String WITH_ARGS_METHOD_NAME = "withArgs";
  private static final Class<?> OBJECT_ARRAY_CLASS = new Object[] {}.getClass();

  private Reflection() {}

  public static ReflectionInfo reflect(TyResImplementation implementation, Class<?> bundleClass) {
    var bundleInfo = reflectBundle(implementation, bundleClass);
    // note: we also include inherited methods.
    var methods = bundleClass.getMethods();
    var resources = resReflectionInfoFromMethods(bundleInfo, methods);
    return new DefaultReflectionInfo(resources, bundleInfo);
  }

  private static Collection<ResReflectionInfo> resReflectionInfoFromMethods(
    DefaultBundleInfo bundle,
    Method[] methods
  ) {
    var stream = Arrays
      .stream(methods)
      .<ResReflectionInfo>map(method -> {
        var resInfo = reflectMethod(bundle, method);
        final ReturnValueConstructor returnValueConstructor = createDefaultReturnTypeConstructor(
          bundle,
          resInfo
        );
        return new DefaultResReflectionInfo(resInfo, returnValueConstructor);
      });
    return stream.toList();
  }

  private static ResInfo reflectMethod(DefaultBundleInfo bundle, Method method) {
    var name = getNameFrom(method);
    var defaultValue = getDefaultValueFrom(method);
    return new DefaultResInfo(bundle, method, name, defaultValue);
  }

  private static DefaultBundleInfo reflectBundle(
    TyResImplementation implementation,
    Class<?> bundleClass
  ) {
    var customPackage = customPackage(bundleClass);
    return new DefaultBundleInfo(bundleClass, customPackage, implementation);
  }

  private static DefaultReturnTypeConstructor createDefaultReturnTypeConstructor(
    BundleInfo bundleInfo,
    ResInfo resInfo
  ) {
    var method = resInfo.method();
    // make sure the return type is of correct type
    assertNewResIsAssignableFromReturnType(method);
    return createReturnTypeCreator(method);
  }

  private static void assertNewResIsAssignableFromReturnType(Method method) {
    var returnType = method.getReturnType();
    if (!Res.class.isAssignableFrom(returnType)) {
      var methodName = method.getName();
      var declaringClass = method.getDeclaringClass();
      throw new TyResException(
        "Invalid return type for method '" +
        methodName +
        "' (declaring class '" +
        declaringClass.getName() +
        "'). Return type must be " +
        "of a class that implements " +
        Res.class.getSimpleName() +
        ". Got '" +
        returnType.getName() +
        "'."
      );
    }
  }

  private static DefaultReturnTypeConstructor createReturnTypeCreator(Method method) {
    var returnType = method.getReturnType();
    final Method createMethod;
    try {
      createMethod =
        returnType.getDeclaredMethod(CREATE_METHOD_NAME, BundleInfo.class, ResInfo.class);
    } catch (Exception exception) {
      throw new TyResException(
        "Class '" +
        returnType +
        "' does not implement static method called " +
        CREATE_METHOD_NAME +
        " (arguments " +
        BundleInfo.class.getSimpleName() +
        ", " +
        ResInfo.class.getSimpleName() +
        ") - or it's not accesible by reflection.",
        exception
      );
    }
    var createMethodReturnType = createMethod.getReturnType();
    if (!returnType.isAssignableFrom(createMethodReturnType)) {
      throw new TyResException(
        "Method " +
        CREATE_METHOD_NAME +
        " in '" +
        returnType +
        "'' must return a type where '" +
        returnType +
        "' is assignable from; It's not assignable from the given type '" +
        createMethodReturnType +
        "'."
      );
    }
    // also make sure that the 'withArgs' method is correct
    final Method withArgsMethod;
    try {
      withArgsMethod = returnType.getDeclaredMethod(WITH_ARGS_METHOD_NAME, OBJECT_ARRAY_CLASS);
    } catch (Exception exception) {
      throw new TyResException(
        "Class '" +
        returnType +
        "' has missing or inaccessible method '" +
        WITH_ARGS_METHOD_NAME +
        "'. Note: if the class '" +
        returnType.getSimpleName() +
        "' is abstract, it must declare this method as abstract with the correct return" +
        " type (inherited methods from the interface are not included in this check).",
        exception
      );
    }
    var withArgsMethodReturnType = withArgsMethod.getReturnType();
    if (!withArgsMethodReturnType.isAssignableFrom(withArgsMethodReturnType)) {
      throw new TyResException(
        "Method " +
        WITH_ARGS_METHOD_NAME +
        " in '" +
        returnType +
        "'' must return a type where '" +
        returnType +
        "' is assignable from; It's not assignable from the given type '" +
        withArgsMethodReturnType +
        "'."
      );
    }
    // everything is ok
    return new DefaultReturnTypeConstructor(createMethod);
  }

  private static String getNameFrom(Method method) {
    var maybeAnnotation = method.getAnnotation(Name.class);
    if (maybeAnnotation != null) {
      return maybeAnnotation.value();
    } else {
      return method.getName();
    }
  }

  @Nullable
  private static String getDefaultValueFrom(Method method) {
    var maybeAnnotation = method.getAnnotation(Default.class);
    if (maybeAnnotation != null) {
      return maybeAnnotation.value();
    } else {
      return null;
    }
  }

  @Nullable
  private static List<String> customPackage(Class<?> bundleClass) {
    var packageAnnotation = bundleClass.getAnnotation(Package.class);
    if (packageAnnotation == null) {
      return null;
    } else {
      var values = packageAnnotation.value();
      if (values != null) {
        if (values.length == 1) {
          return List.of(values[0]);
        } else {
          return Arrays.stream(values).collect(Collectors.toUnmodifiableList());
        }
      } else {
        throw new TyResException(
          "Annotation " + Package.class + " MUST NOT have a null-value. See " + bundleClass + "."
        );
      }
    }
  }

  private static final class DefaultReflectionInfo implements ReflectionInfo {

    private final DefaultBundleInfo bundleInfo;
    private final Collection<ResReflectionInfo> resources;

    private DefaultReflectionInfo(
      Collection<ResReflectionInfo> resources,
      DefaultBundleInfo bundleInfo
    ) {
      this.resources = resources;
      this.bundleInfo = bundleInfo;
    }

    @Override
    public BundleInfo bundleInfo() {
      return bundleInfo;
    }

    @Override
    public Collection<ResReflectionInfo> resources() {
      return resources;
    }
  }

  private static final class DefaultResReflectionInfo implements ResReflectionInfo {

    private final ResInfo resInfo;

    public DefaultResReflectionInfo(
      ResInfo resInfo,
      ReturnValueConstructor returnValueConstructor
    ) {
      this.resInfo = resInfo;
      this.returnValueConstructor = returnValueConstructor;
    }

    private final ReturnValueConstructor returnValueConstructor;

    @Override
    public ResInfo resInfo() {
      return resInfo;
    }

    @Override
    public ReturnValueConstructor returnValueConstructor() {
      return returnValueConstructor;
    }
  }

  private static final class DefaultBundleInfo implements BundleInfo {

    private final Class<?> bundleClass;
    private final List<String> customPackage;
    private final TyResImplementation implementation;

    private DefaultBundleInfo(
      Class<?> bundleClass,
      List<String> customPackage,
      TyResImplementation implementation
    ) {
      this.bundleClass = bundleClass;
      this.customPackage = customPackage;
      this.implementation = implementation;
    }

    @Override
    public Class<?> bundleClass() {
      return bundleClass;
    }

    @Nullable
    @Override
    public List<String> customPackage() {
      return customPackage;
    }

    @Override
    public TyResImplementation implementation() {
      return implementation;
    }
  }

  private static final class DefaultResInfo implements ResInfo {

    private final DefaultBundleInfo bundleInfo;
    private final Method method;
    private final String name;

    @Nullable
    private final String defaultValue;

    private DefaultResInfo(
      DefaultBundleInfo bundleInfo,
      Method method,
      String name,
      @Nullable String defaultValue
    ) {
      this.bundleInfo = bundleInfo;
      this.method = method;
      this.name = name;
      this.defaultValue = defaultValue;
    }

    @Override
    public BundleInfo bundle() {
      return bundleInfo;
    }

    @Override
    public Method method() {
      return method;
    }

    @Override
    public String name() {
      return name;
    }

    @Nullable
    @Override
    public String defaultValue() {
      return defaultValue;
    }
  }

  private static final class DefaultReturnTypeConstructor implements ReturnValueConstructor {

    public DefaultReturnTypeConstructor(Method createMethod) {
      this.createMethod = createMethod;
    }

    private final Method createMethod;

    @Override
    public Res<?> construct(BundleInfo bundleInfo, ResInfo resInfo) {
      try {
        return (Res<?>) this.createMethod.invoke(null, bundleInfo, resInfo);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new TyResException(
          "Unable to invoke the '" +
          CREATE_METHOD_NAME +
          "' on the return type. Method is '" +
          createMethod +
          "'.",
          e
        );
      }
    }
  }
}
