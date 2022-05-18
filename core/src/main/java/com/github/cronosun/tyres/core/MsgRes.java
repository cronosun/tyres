package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public abstract class MsgRes implements Res<MsgRes, MsgMarker>, Msg {

  private MsgRes() {}

  public static MsgRes create(ResInfo resInfo) {
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
  public String message(
    Resources resources,
    Resources.NotFoundStrategy notFoundStrategy,
    Locale locale
  ) {
    return resources.msg(this, notFoundStrategy, locale);
  }

  @Override
  public @Nullable String maybeMessage(Resources resources, Locale locale) {
    return resources.maybeMsg(this, locale);
  }

  private static final class NoArgs extends MsgRes {

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
    public MsgRes withArgs(Object[] args) {
      if (args.length == 0) {
        return this;
      }
      return new WithArgs(info, args);
    }
  }

  private static final class WithArgs extends MsgRes {

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
    public MsgRes withArgs(Object[] args) {
      if (args.length == 0) {
        return new NoArgs(info);
      } else {
        return new WithArgs(info, args);
      }
    }
  }

  @Override
  public final String toString() {
    return "MsgRes{" + "info=" + info() + ", args=" + Arrays.toString(args()) + '}';
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (other instanceof MsgRes) {
      var castOther = (MsgRes) other;
      return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
    } else {
      return false;
    }
  }

  @Override
  public final int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    result = 31 * result + MsgRes.class.hashCode();
    return result;
  }
}
