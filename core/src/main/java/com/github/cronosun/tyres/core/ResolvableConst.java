package com.github.cronosun.tyres.core;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

final class ResolvableConst<T> implements Resolvable {

  @Nullable
  private transient volatile Text cachedText;

  @Nullable
  private transient volatile String cachedConciseDebugString;

  private ResolvableConst(Class<T> bundleClass, Function<T, Text> function) {
    this.bundleClass = bundleClass;
    this.function = function;
  }

  public static <T> ResolvableConst<T> of(Class<T> bundleClass, Function<T, Text> function) {
    return new ResolvableConst<>(bundleClass, function);
  }

  private final Class<T> bundleClass;
  private final Function<T, Text> function;

  @Override
  public Text resolve(Resources resources) {
    var cachedText = this.cachedText;
    if (cachedText != null) {
      return cachedText;
    }
    var bundle = resources.get(this.bundleClass);
    cachedText = this.function.apply(bundle);
    this.cachedText = cachedText;
    return cachedText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResolvableConst<?> that = (ResolvableConst<?>) o;
    return bundleClass.equals(that.bundleClass) && function.equals(that.function);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleClass, function);
  }

  @Override
  public String conciseDebugString() {
    var maybeCachedText = this.cachedText;
    if (maybeCachedText != null) {
      return maybeCachedText.conciseDebugString();
    } else {
      var maybeCachedConciseDebugString = this.cachedConciseDebugString;
      if (maybeCachedConciseDebugString != null) {
        return maybeCachedConciseDebugString;
      } else {
        // that's a bit tricky to get the correct information. We create a proxy to get the method name
        Object[] callInformation = null;
        try {
          callInformation = tryToExtractCallInformation();
        } catch (Exception ignored) {}
        final String newConciseDebugString;
        if (callInformation != null) {
          newConciseDebugString =
            WithConciseDebugString.build(
              List.of("resolvable", bundleClass.getSimpleName(), callInformation)
            );
        } else {
          newConciseDebugString =
            WithConciseDebugString.build(List.of("resolvable", bundleClass.getSimpleName()));
        }
        this.cachedConciseDebugString = newConciseDebugString;
        return newConciseDebugString;
      }
    }
  }

  @Override
  public String toString() {
    return (
      "ResolvableConst{" +
      "conciseDebugString=" +
      conciseDebugString() +
      ", bundleClass=" +
      bundleClass +
      ", function=" +
      function +
      '}'
    );
  }

  private Object[] tryToExtractCallInformation() {
    // that's a bit tricky to get the correct information. We create a proxy to get the method name
    final Object[] callInformation = new Object[] { null, null };
    @SuppressWarnings("unchecked")
    var proxy = (T) Proxy.newProxyInstance(
      this.bundleClass.getClassLoader(),
      new Class[] { this.bundleClass },
      (_ignored, method, args) -> {
        callInformation[0] = method.getName();
        callInformation[1] = args;
        return TextForConciseDebugString.INSTANCE;
      }
    );
    this.function.apply(proxy);
    return callInformation;
  }

  private static final class TextForConciseDebugString implements Fmt {

    private static final TextForConciseDebugString INSTANCE = new TextForConciseDebugString();

    @Override
    public String conciseDebugString() {
      return "";
    }

    @Override
    public String getText(
      @Nullable Locale locale,
      NotFoundConfig.WithNullAndDefault notFoundConfig
    ) {
      return "";
    }
  }
}
