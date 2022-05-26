package com.github.cronosun.tyres.core.experiment;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

final class ResolvableConst<T> implements Resolvable {

  @Nullable
  private transient volatile Text cachedText;

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
  public Text get(Resources2 resources) {
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
}
