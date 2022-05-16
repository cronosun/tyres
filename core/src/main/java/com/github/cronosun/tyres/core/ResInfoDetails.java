package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

public abstract class ResInfoDetails {
    private ResInfoDetails() {
    }

    abstract Kind kind();

    public enum Kind {
        /**
         * A resource that produces a string (when formatted possibly a message).
         */
        STRING,
        /**
         * A resouce that produces a file.
         */
        FILE
    }

    public abstract StringResource asStringResouce();
    public abstract FileResouce asFileResource();

    public static final class StringResource extends ResInfoDetails {
        private final String name;
        @Nullable
        private final String defaultValue;

        public StringResource(String name, @Nullable String defaultValue) {
            this.name = Objects.requireNonNull(name);
            this.defaultValue = defaultValue;
        }

        @Override
        Kind kind() {
            return Kind.STRING;
        }

        @Override
        public StringResource asStringResouce() {
            return this;
        }

        @Override
        public FileResouce asFileResource() {
            throw new TyResException("This is not a file resouce.");
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
            StringResource that = (StringResource) o;
            return name.equals(that.name) && Objects.equals(defaultValue, that.defaultValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, defaultValue);
        }

        @Override
        public String toString() {
            return "StringResource{" +
                    "name='" + name + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    '}';
        }
    }

    public static final class FileResouce extends ResInfoDetails {
        private final String filename;

        public FileResouce(String filename) {
            this.filename = Objects.requireNonNull(filename);
        }

        @Override
        Kind kind() {
            return Kind.FILE;
        }

        @Override
        public StringResource asStringResouce() {
            throw new TyResException("This is not a string resouce");
        }

        @Override
        public FileResouce asFileResource() {
            return this;
        }

        public String filename() {
            return filename;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileResouce that = (FileResouce) o;
            return filename.equals(that.filename);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filename);
        }

        @Override
        public String toString() {
            return "FileResouce{" +
                    "filename='" + filename + '\'' +
                    '}';
        }
    }
}
