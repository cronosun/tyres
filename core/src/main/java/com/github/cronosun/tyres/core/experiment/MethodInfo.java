package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public final class MethodInfo implements WithConciseDebugString {

  private static final Class<?>[] KNOWN_RETURN_TYPES = new Class[] {
    Text.class,
    Fmt.class,
    Bin.class,
  };

  private final Method method;

  @Nullable
  private final Default defaultAnnotation;

  @Nullable
  private final Rename renameAnnotation;

  @Nullable
  private final File fileAnnotation;

  @Nullable
  private final Filename filename;

  private final ReturnType returnType;
  private final String name;
  private final int numberOfArguments;

  private MethodInfo(
    Method method,
    @Nullable Default defaultAnnotation,
    @Nullable Rename renameAnnotation,
    @Nullable File fileAnnotation,
    @Nullable Filename filename,
    ReturnType returnType,
    String name,
    int numberOfArguments
  ) {
    this.method = method;
    this.defaultAnnotation = defaultAnnotation;
    this.renameAnnotation = renameAnnotation;
    this.fileAnnotation = fileAnnotation;
    this.filename = filename;
    this.returnType = returnType;
    this.name = name;
    this.numberOfArguments = numberOfArguments;
  }

  public static MethodInfo reflect(Method method) {
    var defaultAnnotation = method.getAnnotation(Default.class);
    var renameAnnotation = method.getAnnotation(Rename.class);
    var fileAnnotation = method.getAnnotation(File.class);
    var filename = determineFilename(method, fileAnnotation);
    var returnType = determineReturnType(method);
    var name = determineName(method, renameAnnotation);
    var numberOfArguments = method.getParameterCount();
    return new MethodInfo(
      method,
      defaultAnnotation,
      renameAnnotation,
      fileAnnotation,
      filename,
      returnType,
      name,
      numberOfArguments
    );
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(List.of(name()));
  }

  public enum ReturnType {
    TEXT(BaseType.TEXT),
    FMT(BaseType.TEXT),
    BIN(BaseType.BIN),
    UNKNOWN(BaseType.UNKNOWN);

    private final BaseType base;

    ReturnType(BaseType base) {
      this.base = base;
    }

    public BaseType base() {
      return base;
    }
  }

  public enum BaseType {
    TEXT,
    BIN,
    UNKNOWN,
  }

  public void assertValid() {
    var returnType = this.returnType();
    var base = returnType.base();
    switch (base) {
      case TEXT:
        if (filename() != null) {
          throw new TyResException(
            "Text resource '" +
            method.getName() +
            "' has a filename; The '@" +
            File.class.getSimpleName() +
            "' annotation is not supported for text resources."
          );
        }
        switch (returnType) {
          case TEXT:
            if (numberOfArguments() != 0) {
              var arguments = Arrays
                .stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
              throw new TyResException(
                "Text resource '" +
                method.getName() +
                "' (not formatted) cannot have arguments (got [" +
                arguments +
                "]). if you want to have arguments, use a formatted text, use '" +
                Fmt.class.getSimpleName() +
                "' as return type."
              );
            }
            break;
          case FMT:
            // no check required
            break;
          case BIN:
          case UNKNOWN:
            throw new TyResException("unknown return type for text: " + returnType);
        }
        break;
      case BIN:
        if (filename() == null) {
          throw new TyResException(
            "Binary resource '" +
            method.getName() +
            "' must have a '@" +
            File.class.getSimpleName() +
            "' annotation (it's missing)."
          );
        }
        if (defaultAnnotation() != null) {
          throw new TyResException(
            "Binary resource '" +
            method.getName() +
            "' cannot have the '@" +
            Default.class.getSimpleName() +
            "' annotation (it's for text resources only)."
          );
        }
        if (renameAnnotation() != null) {
          throw new TyResException(
            "Binary resource '" +
            method.getName() +
            "' cannot have the '@" +
            Rename.class.getSimpleName() +
            "' annotation (it's for text resources only)."
          );
        }
        if (numberOfArguments() != 0) {
          var arguments = Arrays
            .stream(method.getParameterTypes())
            .map(Class::getSimpleName)
            .collect(Collectors.joining(", "));
          throw new TyResException(
            "Binary resource '" +
            method.getName() +
            "' cannot have arguments (got [" +
            arguments +
            "])."
          );
        }
        break;
      case UNKNOWN:
        var knownReturnTypes = Arrays
          .stream(KNOWN_RETURN_TYPES)
          .map(Class::getSimpleName)
          .collect(Collectors.joining(", "));
        throw new TyResException(
          "Method '" +
          method.getName() +
          "' has unsupported return type '" +
          method.getReturnType().getSimpleName() +
          "'; use one of the supported return types: [" +
          knownReturnTypes +
          "]."
        );
    }
  }

  public Method method() {
    return method;
  }

  @Nullable
  public Default defaultAnnotation() {
    return defaultAnnotation;
  }

  @Nullable
  public Rename renameAnnotation() {
    return renameAnnotation;
  }

  @Nullable
  public File fileAnnotation() {
    return fileAnnotation;
  }

  @Nullable
  public Filename filename() {
    return filename;
  }

  public ReturnType returnType() {
    return returnType;
  }

  public String name() {
    return name;
  }

  public int numberOfArguments() {
    return numberOfArguments;
  }

  private static String determineName(Method method, @Nullable Rename rename) {
    if (rename == null) {
      return method.getName();
    } else {
      return rename.value();
    }
  }

  private static Filename determineFilename(Method method, @Nullable File file) {
    if (file != null) {
      return Filename.from(file.value());
    } else {
      return null;
    }
  }

  private static ReturnType determineReturnType(Method method) {
    var returnTypeClass = method.getReturnType();
    final ReturnType returnType;
    if (returnTypeClass.equals(Text.class)) {
      return ReturnType.TEXT;
    } else if (returnTypeClass.equals(Fmt.class)) {
      return ReturnType.FMT;
    } else if (returnTypeClass.equals(Bin.class)) {
      return ReturnType.BIN;
    } else {
      return ReturnType.UNKNOWN;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MethodInfo that = (MethodInfo) o;
    return method.equals(that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method);
  }

  @Override
  public String toString() {
    return (
      "MethodInfo{" +
      "method=" +
      method +
      ", defaultAnnotation=" +
      defaultAnnotation +
      ", renameAnnotation=" +
      renameAnnotation +
      ", fileAnnotation=" +
      fileAnnotation +
      ", filename=" +
      filename +
      ", returnType=" +
      returnType +
      ", name='" +
      name +
      '\'' +
      ", numberOfArguments=" +
      numberOfArguments +
      '}'
    );
  }
}
