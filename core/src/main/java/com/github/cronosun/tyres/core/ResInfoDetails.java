package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public abstract class ResInfoDetails {

  private ResInfoDetails() {}

  public abstract Kind kind();

  public abstract StrResource asStringResource();

  public abstract BinResource asBinResource();

  public enum Kind {
    /**
     * A resource that produces a string (when formatted possibly a message).
     */
    STRING,
    /**
     * A resource that produces a binary stream (usually from a file).
     */
    BINARY,
  }

  public static final class StrResource extends ResInfoDetails {

    private final String name;

    @Nullable
    private final String defaultValue;

    public StrResource(String name, @Nullable String defaultValue) {
      this.name = Objects.requireNonNull(name);
      this.defaultValue = defaultValue;
    }

    @Override
    public Kind kind() {
      return Kind.STRING;
    }

    @Override
    public StrResource asStringResource() {
      return this;
    }

    @Override
    public BinResource asBinResource() {
      throw new TyResException("This is not a binary resource.");
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
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StrResource that = (StrResource) o;
      return name.equals(that.name) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, defaultValue);
    }

    @Override
    public String toString() {
      return (
        "StringResource{" + "name='" + name + '\'' + ", defaultValue='" + defaultValue + '\'' + '}'
      );
    }
  }

  public static final class BinResource extends ResInfoDetails {

    private final Filename filename;

    public BinResource(String filename) {
      this.filename = Filename.from(Objects.requireNonNull(filename));
    }

    @Override
    public Kind kind() {
      return Kind.BINARY;
    }

    @Override
    public StrResource asStringResource() {
      throw new TyResException("This is not a string resource.");
    }

    @Override
    public BinResource asBinResource() {
      return this;
    }

    public Filename filename() {
      return filename;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BinResource that = (BinResource) o;
      return filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
      return Objects.hash(filename);
    }

    @Override
    public String toString() {
      return "BinResource{" + "filename='" + filename + '\'' + '}';
    }
  }
}
