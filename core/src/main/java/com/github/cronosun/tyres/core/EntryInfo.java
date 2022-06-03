package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * Information about a single entry in a bundle.
 */
@ThreadSafe
public abstract class EntryInfo implements WithConciseDebugString {

  private static final Class<?>[] KNOWN_TEXT_TYPES = new Class[] { Text.class, Fmt.class };

  private EntryInfo(
    BundleInfo bundleInfo,
    Method method,
    boolean required,
    @Nullable Object implementationData
  ) {
    this.bundleInfo = Objects.requireNonNull(bundleInfo);
    this.method = Objects.requireNonNull(method);
    this.required = required;
    this.implementationData = implementationData;
  }

  private final BundleInfo bundleInfo;
  private final Method method;

  private final boolean required;

  @Nullable
  private final Object implementationData;

  public final Method method() {
    return this.method;
  }

  public final BundleInfo bundleInfo() {
    return bundleInfo;
  }

  /**
   * <code>true</code> if this entry is required (default). It's used for validation. See {@link Validation#optional()}
   * annotation.
   */
  public final boolean required() {
    return required;
  }

  /**
   * This is additional data the implementation can add (optionally). This is implementation specific and should not be
   * used, unless you write a {@link Resources} implementation.
   */
  @Nullable
  public Object implementationData() {
    return this.implementationData;
  }

  @ThreadSafe
  public static final class TextEntry extends EntryInfo {

    private final TextType type;
    private final String name;

    @Nullable
    private final String defaultValue;

    public TextEntry(
      BundleInfo bundleInfo,
      Method method,
      boolean required,
      @Nullable Object implementationData,
      TextType type,
      String name,
      @Nullable String defaultValue
    ) {
      super(bundleInfo, method, required, implementationData);
      this.type = Objects.requireNonNull(type);
      this.name = Objects.requireNonNull(name);
      this.defaultValue = defaultValue;
    }

    private TextEntry withImplementationData(@Nullable Object implementationData) {
      if (implementationData != null || this.implementationData() != null) {
        return new TextEntry(
          bundleInfo(),
          method(),
          required(),
          implementationData,
          type(),
          name(),
          defaultValue()
        );
      } else {
        return this;
      }
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

    @Nullable
    public String defaultValue() {
      return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      TextEntry that = (TextEntry) o;
      return (
        type == that.type &&
        name.equals(that.name) &&
        Objects.equals(defaultValue, that.defaultValue)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), type, name, defaultValue);
    }

    @Override
    public String toString() {
      return (
        "TextEntryInfo{" +
        "type=" +
        type +
        ", name='" +
        name +
        '\'' +
        ", defaultValue='" +
        defaultValue +
        '\'' +
        ", bundleInfo=" +
        bundleInfo() +
        ", method=" +
        method() +
        ", required=" +
        required() +
        ", implementationData=" +
        implementationData() +
        '}'
      );
    }

    @Override
    public String conciseDebugString() {
      return WithConciseDebugString.build(List.of(bundleInfo(), method().getName()));
    }
  }

  @ThreadSafe
  public static final class BinEntry extends EntryInfo {

    private final Filename filename;

    public BinEntry(
      BundleInfo bundleInfo,
      Method method,
      boolean required,
      @Nullable Object implementationData,
      Filename filename
    ) {
      super(bundleInfo, method, required, implementationData);
      this.filename = Objects.requireNonNull(filename);
    }

    private BinEntry withImplementationData(@Nullable Object implementationData) {
      if (implementationData != null || this.implementationData() != null) {
        return new BinEntry(bundleInfo(), method(), required(), implementationData, filename());
      } else {
        return this;
      }
    }

    /**
     * The filename as declared in the bundle, see {@link File} annotation.
     */
    public Filename filename() {
      return filename;
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
      BinEntry that = (BinEntry) o;
      return filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), filename);
    }

    @Override
    public String toString() {
      return (
        "BinEntryInfo{" +
        "filename=" +
        filename +
        ", bundleInfo=" +
        bundleInfo() +
        ", method=" +
        method() +
        ", required=" +
        required() +
        ", implementationData=" +
        implementationData() +
        '}'
      );
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EntryInfo entryInfo = (EntryInfo) o;
    return (
      required == entryInfo.required &&
      bundleInfo.equals(entryInfo.bundleInfo) &&
      method.equals(entryInfo.method) &&
      Objects.equals(implementationData, entryInfo.implementationData)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleInfo, method, required, implementationData);
  }

  public enum TextType {
    TEXT,
    FMT,
  }

  /**
   * Gets {@link EntryInfo} by using reflection. This is the default implementation - implementations of
   * {@link Resources} can use a different implementation, but should behave the same.
   *
   * Throws {@link TyResException} if the method is invalid.
   */
  public static EntryInfo reflect(
    BundleInfo bundleInfo,
    Method method,
    ImplementationDataProvider implementationDataProvider
  ) {
    var fileAnnotation = method.getAnnotation(File.class);
    var filename = determineFilename(fileAnnotation);
    if (filename == null) {
      // if it has no filename, it must be a text
      var entryInfo = newText(bundleInfo, method);
      return entryInfo.withImplementationData(
        implementationDataProvider.implementationDataForTextEntry(entryInfo)
      );
    } else {
      var entryInfo = newBin(bundleInfo, method, filename);
      return entryInfo.withImplementationData(
        implementationDataProvider.implementationDataForBinEntry(entryInfo)
      );
    }
  }

  private static BinEntry newBin(BundleInfo bundleInfo, Method method, Filename filename) {
    var required = isRequired(method);
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
    if (!returnType.equals(Bin.class)) {
      throw new TyResException(
        "Method '" +
        method.getName() +
        "' in '" +
        bundleInfo.bundleClass().getSimpleName() +
        "' has return type '" +
        returnType.getSimpleName() +
        "'; this is invalid for binary resources, must be type '" +
        Bin.class.getSimpleName() +
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
        BinEntry.class +
        "'."
      );
    }
    return new BinEntry(bundleInfo, method, required, null, filename);
  }

  private static TextEntry newText(BundleInfo bundleInfo, Method method) {
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
        TextEntry.class +
        "'."
      );
    }

    var required = isRequired(method);
    var renameAnnotation = method.getAnnotation(Rename.class);
    var name = determineName(method, renameAnnotation);
    var defaultValueAnnotation = method.getAnnotation(Default.class);
    final String defaultValue;
    if (defaultValueAnnotation != null) {
      defaultValue = defaultValueAnnotation.value();
    } else {
      defaultValue = null;
    }
    return new TextEntry(bundleInfo, method, required, null, textType, name, defaultValue);
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

  private static boolean isRequired(Method method) {
    var validationAnnotation = method.getAnnotation(Validation.class);
    if (validationAnnotation == null) {
      // default value: required = true
      return true;
    } else {
      return !validationAnnotation.optional();
    }
  }

  @Nullable
  private static TextType determineTextType(Method method) {
    var returnTypeClass = method.getReturnType();
    if (returnTypeClass.equals(Text.class)) {
      return TextType.TEXT;
    } else if (returnTypeClass.equals(Fmt.class)) {
      return TextType.FMT;
    } else {
      return null;
    }
  }
}
