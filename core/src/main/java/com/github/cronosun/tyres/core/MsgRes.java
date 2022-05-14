package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class MsgRes implements Res<MsgRes>, Msg {

  private static final Object[] NO_ARGS = new Object[] {};

  private MsgRes() {}

  public static MsgRes create(BundleInfo _ignored, ResInfo info) {
    return new NoArgs(info);
  }

  public abstract MsgRes withArgs(Object[] args);

  @Override
  public final String message(MsgSource source, MsgSource.NotFoundStrategy notFoundStrategy, Locale locale) {
    return source.message(this, notFoundStrategy, locale);
  }

  @Nullable
  @Override
  public final String maybeMessage(MsgSource source, Locale locale) {
    return source.maybeMessage(this, locale);
  }

  private static final class NoArgs extends MsgRes {

    private final ResInfo resInfo;

    public NoArgs(ResInfo resInfo) {
      this.resInfo = resInfo;
    }

    @Override
    public ResInfo info() {
      return resInfo;
    }

    @Override
    public Object[] args() {
      return NO_ARGS;
    }

    @Override
    public MsgRes withArgs(Object[] args) {
      return new WithArgs(resInfo, args);
    }
  }

  private static final class WithArgs extends MsgRes {

    private final ResInfo resInfo;
    private final Object[] args;

    public WithArgs(ResInfo resInfo, Object[] args) {
      this.resInfo = resInfo;
      this.args = args;
    }

    @Override
    public ResInfo info() {
      return resInfo;
    }

    @Override
    public Object[] args() {
      return args;
    }

    @Override
    public MsgRes withArgs(Object[] args) {
      return new WithArgs(resInfo, args);
    }
  }
}
