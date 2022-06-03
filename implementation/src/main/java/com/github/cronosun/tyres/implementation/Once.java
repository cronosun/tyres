package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.ThreadSafe;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;

@ThreadSafe
public abstract class Once<T> implements Supplier<T> {

  /**
   * Cannot be constructed from outside.
   */
  private Once() {}

  public static <T> Once<T> fromSupplier(Supplier<T> supplier) {
    return new OnceFromSupplier<>(supplier);
  }

  public static <T> Once<T> fromValue(T value) {
    return new OnceFromValue<>(value);
  }

  private static final class OnceFromSupplier<T> extends Once<T> {

    @Nullable
    private volatile Supplier<T> supplier;
    private final Object lock = new Object();

    @Nullable
    private volatile T value;

    private OnceFromSupplier(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public T get() {
      var value = this.value;
      if (value != null) {
        return value;
      } else {
        synchronized (this.lock) {
          value = this.value;
          if (value == null) {
            var supplier = Objects.requireNonNull(this.supplier, "Supplier is null.");
            value = supplier.get();
            this.value = value;
            // we don't need the supplier anymore (potentially allows the GC to free some resources)
            this.supplier = null;
          }
          return value;
        }
      }
    }
  }

  private static final class OnceFromValue<T> extends Once<T> {

    private final T value;

    private OnceFromValue(T value) {
      this.value = value;
    }

    @Override
    public T get() {
      return value;
    }
  }
}
