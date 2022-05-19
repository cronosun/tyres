package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
abstract class MsgResDefault implements Res<MsgRes>, MsgRes {

  private MsgResDefault() {}

  public static MsgResDefault create(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    if (kind == ResInfoDetails.Kind.STRING) {
      return new NoArgs(resInfo);
    } else {
      throw new TyResException(
        "Message resource is not compatible with " +
        kind +
        ". Must be of kind " +
        ResInfoDetails.Kind.STRING +
        "'."
      );
    }
  }

  @Override
  public final String toString() {
    return "MsgRes{" + "info=" + info() + ", args=" + Arrays.toString(args()) + '}';
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (other instanceof MsgResDefault) {
      var castOther = (MsgResDefault) other;
      return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
    } else {
      return false;
    }
  }

  @Override
  public final int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    result = 31 * result + MsgResDefault.class.hashCode();
    return result;
  }

  private static final class NoArgs extends MsgResDefault {

    private static final Object[] NO_ARGS = new Object[] {};
    private final ResInfo info;

    private NoArgs(ResInfo info) {
      this.info = info;
    }

    @Override
    public ResInfo info() {
      return info;
    }

    @Override
    public Object[] args() {
      return NO_ARGS;
    }

    @Override
    public MsgResDefault withArgs(Object[] args) {
      if (args.length == 0) {
        return this;
      }
      return new WithArgs(info, args);
    }
  }

  private static final class WithArgs extends MsgResDefault {

    private final ResInfo info;
    private final Object[] args;

    private WithArgs(ResInfo info, Object[] args) {
      this.info = info;
      this.args = args;
    }

    @Override
    public ResInfo info() {
      return info;
    }

    @Override
    public Object[] args() {
      return args;
    }

    @Override
    public MsgResDefault withArgs(Object[] args) {
      if (args.length == 0) {
        return new NoArgs(info);
      } else {
        return new WithArgs(info, args);
      }
    }
  }
}
