package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import java.util.Objects;

@ThreadSafe
public abstract class ValidationError {

  private ValidationError() {}

  public static final class ResourceNotFound extends ValidationError {

    private final ResInfo resInfo;
    private final Locale locale;

    public ResourceNotFound(ResInfo resInfo, Locale locale) {
      this.resInfo = resInfo;
      this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ResourceNotFound that = (ResourceNotFound) o;
      return resInfo.equals(that.resInfo) && locale.equals(that.locale);
    }

    @Override
    public int hashCode() {
      return Objects.hash(resInfo, locale);
    }

    @Override
    public String toString() {
      return "ResourceNotFound{" + resInfo.conciseDebugString() + ", locale=" + locale + '}';
    }
  }

  public static final class InvalidMsgPattern extends ValidationError {

    private final ResInfo resInfo;
    private final Locale locale;
    private final String pattern;

    public InvalidMsgPattern(ResInfo resInfo, Locale locale, String pattern) {
      this.resInfo = resInfo;
      this.locale = locale;
      this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InvalidMsgPattern that = (InvalidMsgPattern) o;
      return (
        resInfo.equals(that.resInfo) && locale.equals(that.locale) && pattern.equals(that.pattern)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(resInfo, locale, pattern);
    }

    @Override
    public String toString() {
      return (
        "InvalidMsgPattern{" +
        resInfo.conciseDebugString() +
        ", locale=" +
        locale +
        ", pattern='" +
        pattern +
        '\'' +
        '}'
      );
    }
  }

  public static final class InvalidNumberOfArguments extends ValidationError {

    private final ResInfo resInfo;
    private final Locale locale;
    private final String pattern;
    private final int numberOfArgumentsInMethod;
    private final int numberOfArgumentsAccordingToPattern;

    public InvalidNumberOfArguments(
      ResInfo resInfo,
      Locale locale,
      String pattern,
      int numberOfArgumentsInMethod,
      int numberOfArgumentsAccordingToPattern
    ) {
      this.resInfo = resInfo;
      this.locale = locale;
      this.pattern = pattern;
      this.numberOfArgumentsInMethod = numberOfArgumentsInMethod;
      this.numberOfArgumentsAccordingToPattern = numberOfArgumentsAccordingToPattern;
    }

    @Override
    public String toString() {
      return (
        "InvalidNumberOfArguments{" +
        resInfo.conciseDebugString() +
        ", locale=" +
        locale +
        ", pattern='" +
        pattern +
        '\'' +
        ", numberOfArgumentsInMethod=" +
        numberOfArgumentsInMethod +
        ", numberOfArgumentsAccordingToPattern=" +
        numberOfArgumentsAccordingToPattern +
        '}'
      );
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InvalidNumberOfArguments that = (InvalidNumberOfArguments) o;
      return (
        numberOfArgumentsInMethod == that.numberOfArgumentsInMethod &&
        numberOfArgumentsAccordingToPattern == that.numberOfArgumentsAccordingToPattern &&
        resInfo.equals(that.resInfo) &&
        locale.equals(that.locale) &&
        pattern.equals(that.pattern)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(
        resInfo,
        locale,
        pattern,
        numberOfArgumentsInMethod,
        numberOfArgumentsAccordingToPattern
      );
    }
  }

  public static final class SuperfluousResource extends ValidationError {

    private final BundleInfo bundle;
    private final Locale locale;
    private final String name;

    public SuperfluousResource(BundleInfo bundle, Locale locale, String name) {
      this.bundle = bundle;
      this.locale = locale;
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SuperfluousResource that = (SuperfluousResource) o;
      return bundle.equals(that.bundle) && locale.equals(that.locale) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(bundle, locale, name);
    }

    @Override
    public String toString() {
      return (
        "SuperfluousResource{" +
        bundle.conciseDebugString() +
        ", locale=" +
        locale +
        ", name='" +
        name +
        '\'' +
        '}'
      );
    }
  }
}
