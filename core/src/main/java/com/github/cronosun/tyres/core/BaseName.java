package com.github.cronosun.tyres.core;

import java.util.Objects;
import java.util.Set;

@ThreadSafe
public final class BaseName implements WithConciseDebugString {

  private static final char DOT_SEPARATOR = '.';
  private static final char PATH_SEPARATOR = '/';
  private final String value;
  private final int nameLength;
  private final int packageNameLength;
  private final String path;

  private BaseName(String value, int packageNameLength, int nameLength, String path) {
    this.value = Objects.requireNonNull(value);
    this.packageNameLength = packageNameLength;
    this.nameLength = nameLength;
    this.path = path;
  }

  public static char separator() {
    return DOT_SEPARATOR;
  }

  public static BaseName fromClass(Class<?> cls) {
    var packageName = cls.getPackageName();
    var name = cls.getSimpleName();
    return fromPackageAndName(packageName, name);
  }

  public static BaseName fromPackageAndName(String packageName, String name) {
    final String baseName;
    if (packageName.isEmpty()) {
      baseName = name;
    } else {
      baseName = packageName + "." + name;
    }
    var packageNameLength = packageName.length();
    var nameLength = name.length();
    return new BaseName(baseName, packageNameLength, nameLength, convertToPath(baseName));
  }

  private static String convertToPath(String name) {
    return name.replace(DOT_SEPARATOR, PATH_SEPARATOR);
  }

  /**
   * Returns the base name in this format:
   *
   * <pre>com.org.package.ClassName</pre>
   * ... or if no package is given:
   * <pre>ClassName</pre>
   * ... and if name is empty too, returns an empty string.
   */
  public String value() {
    return value;
  }

  /**
   * Returns {@link #value} as path (using '/' instead of '.' as separator).
   * <p>
   * Instead of something like <pre>com.org.package.ClassName</pre>,
   * returns <pre>com/org/package/ClassName</pre>.
   */
  public String path() {
    return path;
  }

  /**
   * Returns the name, such as <pre>ClassName</pre>.
   */
  public String name() {
    int length = value.length();
    return value.substring(length - nameLength, length);
  }

  /**
   * Returns the package in this format:
   *
   * <pre>com.org.package</pre>
   * or if no package is given, returns an empty string.
   */
  public String packageName() {
    return value.substring(0, packageNameLength);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseName baseName = (BaseName) o;
    return value.equals(baseName.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "BaseName{" + "value='" + value + '\'' + '}';
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(Set.of(value));
  }
}
