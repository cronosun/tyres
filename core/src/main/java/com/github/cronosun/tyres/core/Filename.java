package com.github.cronosun.tyres.core;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public final class Filename implements WithConciseDebugString {

  private static final char EXTENSION_SEPARATOR = '.';
  private final String value;
  private final String base;

  @Nullable
  private final String extension;

  private Filename(String value, String base, @Nullable String extension) {
    this.value = value;
    this.base = base;
    this.extension = extension;
  }

  public static Filename from(String name) {
    var extensionSeparatorPos = name.lastIndexOf(EXTENSION_SEPARATOR);
    if (extensionSeparatorPos == -1) {
      return new Filename(name, name, null);
    } else {
      var extension = name.substring(extensionSeparatorPos + 1);
      var base = name.substring(0, extensionSeparatorPos);
      return new Filename(name, base, extension);
    }
  }

  public String value() {
    return value;
  }

  public String base() {
    return base;
  }

  @Nullable
  public String extension() {
    return extension;
  }

  @Override
  public String toString() {
    return "Filename{'" + value + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Filename filename = (Filename) o;
    return value.equals(filename.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(List.of(value));
  }
}
