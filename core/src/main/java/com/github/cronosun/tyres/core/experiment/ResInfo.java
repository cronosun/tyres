package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public abstract class ResInfo implements WithConciseDebugString {

  private static final Class<?>[] KNOWN_TEXT_TYPES = new Class[] {
    com.github.cronosun.tyres.core.experiment.Text.class,
    Fmt.class,
  };

  private ResInfo(
    BundleInfo bundleInfo,
    Method method,
    @Nullable Validation validationAnnotation
  ) {
    this.bundleInfo = Objects.requireNonNull(bundleInfo);
    this.method = Objects.requireNonNull(method);
    this.validationAnnotation = validationAnnotation;
  }

  private final BundleInfo bundleInfo;
  private final Method method;

  @Nullable
  private final Validation validationAnnotation;

  public final Method method() {
    return this.method;
  }

  public final BundleInfo bundleInfo() {
    return bundleInfo;
  }

  @Nullable
  public final Validation validationAnnotation() {
    return validationAnnotation;
  }

  public final boolean validationOptional() {
    var validationAnnotation = validationAnnotation();
    if (validationAnnotation != null) {
      return validationAnnotation.optional();
    } else {
      return false;
    }
  }

  public static final class TextResInfo extends ResInfo {

    private final TextType type;
    private final String name;
    private final String effectiveName;

    @Nullable
    private final String defaultValue;

    public TextResInfo(
      BundleInfo bundleInfo,
      Method method,
      @Nullable Validation validationAnnotation,
      TextType type,
      String name,
      String effectiveName,
      @Nullable String defaultValue
    ) {
      super(bundleInfo, method, validationAnnotation);
      this.type = Objects.requireNonNull(type);
      this.name = Objects.requireNonNull(name);
      this.effectiveName = Objects.requireNonNull(effectiveName);
      this.defaultValue = defaultValue;
    }

    public TextType type() {
      return type;
    }

    /**
     * The name as declared in the bundle. It's either the method name or the value from {@link Rename}.
     */
    public String name() {
      return name;
    }

    /**
     * It's usually the same as {@link #name()} (but implementations are allowed to rewrite that).
     */
    public String effectiveName() {
      return effectiveName;
    }

    @Nullable
    public String defaultValue() {
      return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      TextResInfo text = (TextResInfo) o;
      return (
        type == text.type &&
        name.equals(text.name) &&
        effectiveName.equals(text.effectiveName) &&
        Objects.equals(defaultValue, text.defaultValue)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), type, name, effectiveName, defaultValue);
    }

    @Override
    public String toString() {
      return (
        "Text{" +
        "type=" +
        type +
        ", name='" +
        name +
        '\'' +
        ", effectiveName='" +
        effectiveName +
        '\'' +
        ", defaultValue='" +
        defaultValue +
        '\'' +
        ", bundleInfo=" +
        bundleInfo() +
        ", method=" +
        method() +
        ", validationAnnotation=" +
        validationAnnotation() +
        '}'
      );
    }

    @Override
    public String conciseDebugString() {
      return WithConciseDebugString.build(List.of(bundleInfo(), method().getName()));
    }
  }

  public static final class BinResInfo extends ResInfo {

    private final Filename filename;
    private final Filename effectiveFilename;

    public BinResInfo(
      BundleInfo bundleInfo,
      Method method,
      @Nullable Validation validationAnnotation,
      Filename filename,
      Filename effectiveFilename
    ) {
      super(bundleInfo, method, validationAnnotation);
      this.filename = Objects.requireNonNull(filename);
      this.effectiveFilename = effectiveFilename;
    }

    /**
     * The filename as declared in the bundle, see {@link File} annotation.
     */
    public Filename filename() {
      return filename;
    }

    /**
     * This is usually the same as {@link #filename()} (unless the implementation chooses to rewrite the filename).
     */
    public Filename effectiveFilename() {
      return effectiveFilename;
    }

    @Override
    public String conciseDebugString() {
      return WithConciseDebugString.build(List.of(bundleInfo(), method().getName(), filename));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      BinResInfo that = (BinResInfo) o;
      return filename.equals(that.filename) && effectiveFilename.equals(that.effectiveFilename);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), filename, effectiveFilename);
    }

    @Override
    public String toString() {
      return (
        "Bin{" +
        "filename=" +
        filename +
        ", effectiveFilename=" +
        effectiveFilename +
        ", bundleInfo=" +
        bundleInfo() +
        ", method=" +
        method() +
        ", validationAnnotation=" +
        validationAnnotation() +
        '}'
      );
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResInfo resInfo = (ResInfo) o;
    return (
      bundleInfo.equals(resInfo.bundleInfo) &&
      method.equals(resInfo.method) &&
      Objects.equals(validationAnnotation, resInfo.validationAnnotation)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleInfo, method, validationAnnotation);
  }

  public enum TextType {
    TEXT,
    FMT,
  }

  /**
   * Gets {@link ResInfo} by using reflection. This is the default implementation - implementations of
   * {@link Resources2} can use a different implementation, but should behave the same.
   *
   * Throws {@link TyResException} if the method is invalid.
   */
  public static ResInfo reflect(
    BundleInfo bundleInfo,
    Method method,
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    var fileAnnotation = method.getAnnotation(File.class);
    var filename = determineFilename(fileAnnotation);
    if (filename == null) {
      // if it has no filename, it must be a text
      return newText(bundleInfo, method, effectiveNameGenerator);
    } else {
      return newBin(bundleInfo, method, filename, effectiveNameGenerator);
    }
  }

  public interface EffectiveNameGenerator {
    String effectiveNameForText(BundleInfo bundleInfo, Method method, String name);
    Filename effectiveNameForBin(BundleInfo bundleInfo, Method method, Filename filename);
  }

  private static BinResInfo newBin(
    BundleInfo bundleInfo,
    Method method,
    Filename filename,
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    var validationAnnotation = method.getAnnotation(Validation.class);
    if (method.getAnnotation(Rename.class) != null) {
      throw new TyResException(
        "Annotation @" +
        Rename.class.getSimpleName() +
        " is not supported for binary resources. See method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "'."
      );
    }
    if (method.getAnnotation(Default.class) != null) {
      throw new TyResException(
        "Annotation @" +
        Default.class.getSimpleName() +
        " is not supported for binary resources. See method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "'."
      );
    }
    var returnType = method.getReturnType();
    if (!returnType.equals(com.github.cronosun.tyres.core.experiment.Bin.class)) {
      throw new TyResException(
        "Method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "' has return type '" +
        returnType.getSimpleName() +
        "'; this is invalid for binary resources, must be type '" +
        com.github.cronosun.tyres.core.experiment.Bin.class.getSimpleName() +
        "'"
      );
    }
    var numberOfArguments = method.getParameterCount();
    if (numberOfArguments > 0) {
      var arguments = Arrays
        .stream(method.getParameterTypes())
        .map(Class::getSimpleName)
        .collect(Collectors.joining(", "));
      throw new TyResException(
        "Method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "' has arguments [" +
        arguments +
        "]. Arguments are only supported for formatted messages (this is binary). If you want a formatted message, return '" +
        Fmt.class.getSimpleName() +
        "' instead of '" +
        BinResInfo.class +
        "'."
      );
    }
    var effectiveFilename = effectiveNameGenerator.effectiveNameForBin(
      bundleInfo,
      method,
      filename
    );
    return new BinResInfo(bundleInfo, method, validationAnnotation, filename, effectiveFilename);
  }

  private static TextResInfo newText(
    BundleInfo bundleInfo,
    Method method,
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    var textType = determineTextType(method);
    if (textType == null) {
      var returnType = method.getReturnType();
      var validReturnTypes = Arrays
        .stream(KNOWN_TEXT_TYPES)
        .map(Class::getSimpleName)
        .collect(Collectors.joining(", "));
      throw new TyResException(
        "Method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "' returns '" +
        returnType.getSimpleName() +
        "'. Text resources must return one of [" +
        validReturnTypes +
        "]."
      );
    }
    var numberOfArguments = method.getParameterCount();
    if (numberOfArguments > 0 && textType == TextType.TEXT) {
      var arguments = Arrays
        .stream(method.getParameterTypes())
        .map(Class::getSimpleName)
        .collect(Collectors.joining(", "));
      throw new TyResException(
        "Method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "' has arguments [" +
        arguments +
        "]. Arguments are only supported for formatted messages. If you want a formatted message, return '" +
        Fmt.class.getSimpleName() +
        "' instead of '" +
        TextResInfo.class +
        "'."
      );
    }

    var validationAnnotation = method.getAnnotation(Validation.class);
    var renameAnnotation = method.getAnnotation(Rename.class);
    var name = determineName(method, renameAnnotation);
    var effectiveName = effectiveNameGenerator.effectiveNameForText(bundleInfo, method, name);
    var defaultValueAnnotation = method.getAnnotation(Default.class);
    final String defaultValue;
    if (defaultValueAnnotation != null) {
      defaultValue = defaultValueAnnotation.value();
    } else {
      defaultValue = null;
    }
    return new TextResInfo(
      bundleInfo,
      method,
      validationAnnotation,
      textType,
      name,
      effectiveName,
      defaultValue
    );
  }

  private static Filename determineFilename(@Nullable File file) {
    if (file != null) {
      return Filename.from(file.value());
    } else {
      return null;
    }
  }

  private static String determineName(Method method, @Nullable Rename rename) {
    if (rename == null) {
      return method.getName();
    } else {
      return rename.value();
    }
  }

  @Nullable
  private static TextType determineTextType(Method method) {
    var returnTypeClass = method.getReturnType();
    if (returnTypeClass.equals(com.github.cronosun.tyres.core.experiment.Text.class)) {
      return TextType.TEXT;
    } else if (returnTypeClass.equals(Fmt.class)) {
      return TextType.FMT;
    } else {
      return null;
    }
  }
}
