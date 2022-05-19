package com.github.cronosun.tyres.core;

public abstract class MsgRes implements Res<MsgRes> {

  private static final Object[] NO_ARGS = new Object[] {};

  private MsgRes() {}

  public static final MsgRes create(BundleInfo _ignored, ResInfo info) {
    return new NoArgs(info);
  }

  public abstract MsgRes withArgs(Object[] args);

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
