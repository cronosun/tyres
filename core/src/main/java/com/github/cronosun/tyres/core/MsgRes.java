package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public abstract class MsgRes implements Res<MsgRes>, Msg {

  private MsgRes() {}

  public static MsgRes create(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    if (kind == ResInfoDetails.Kind.STRING) {
      return new NoArgs(resInfo);
    } else {
      throw new TyResException(
        "Message resouce is not compatible with " +
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
    return resources.message(this, notFoundStrategy, locale);
  }

  @Override
  public @Nullable String maybeMessage(Resources resources, Locale locale) {
    return resources.maybeMessage(this, locale);
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

    @Override
    public String toString() {
      return "NoArgs{" + "info=" + info + '}';
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

    @Override
    public String toString() {
      return "WithArgs{" + "info=" + info + ", args=" + Arrays.toString(args) + '}';
    }
  }
}
