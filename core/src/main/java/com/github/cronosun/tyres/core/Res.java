package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Identifies a resource.
 *
 * @param <TType> This can be used to give the resource some level of type safety.
 */
@ThreadSafe
public abstract class Res<TType> {

  private Res() {
  }

  public static <T> Res<T> from(ResInfo resInfo) {
    return new NoArgs<>(resInfo);
  }

  public abstract ResInfo info();
  public final Res<TType> withArgs(Object[] args) {
    if (args.length==0) {
      return new NoArgs<>(info());
    } else {
      return new WithArgs<>(info(), args);
    }
  }
  public abstract Object[] args();

  private static final class NoArgs<T> extends Res<T> {
    private static final Object[] ARGS = new Object[]{};
    private final ResInfo info;

    private NoArgs(ResInfo info) {
      this.info = Objects.requireNonNull(info);
    }

    @Override
    public ResInfo info() {
      return info;
    }

    @Override
    public Object[] args() {
      return ARGS;
    }
  }

  private static final class WithArgs<T> extends Res<T> {
    private final ResInfo info;
    private final Object[] args;

    private WithArgs(ResInfo info, Object[] args) {
      this.info = Objects.requireNonNull(info);
      this.args = Objects.requireNonNull(args);
    }

    @Override
    public ResInfo info() {
      return info;
    }

    @Override
    public Object[] args() {
      return args;
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Res<?> that = (Res<?>) o;
    return info().equals(that.info()) && Arrays.equals(args(), that.args());
  }

  @Override
  public final int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    return result;
  }

  @Override
  public String toString() {
    var info = info();
    var args = args();
    return "Res{"+info+" "+Arrays.toString(args)+"}";
  }
}
