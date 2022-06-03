package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

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

    private final Supplier<T> supplier;
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
            value = this.supplier.get();
            this.value = value;
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
