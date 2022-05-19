package com.github.cronosun.tyres.core;

public abstract class TextRes implements Res<TextRes> {

  private static final Object[] NO_ARGS = new Object[] {};

  private TextRes() {}

  public static final TextRes create(BundleInfo _ignored, ResInfo info) {
    return new NoArgs(info);
  }

  public abstract TextRes withArgs(Object[] args);

  private static final class NoArgs extends TextRes {

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
    public TextRes withArgs(Object[] args) {
      return new WithArgs(resInfo, args);
    }
  }

  private static final class WithArgs extends TextRes {

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
    public TextRes withArgs(Object[] args) {
      return new WithArgs(resInfo, args);
    }
  }
}
