package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
final class StrResDefault implements Res<StrRes>, StrRes {

  private static final Object[] NO_ARGS = new Object[] {};
  private final ResInfo resInfo;

  private StrResDefault(ResInfo resInfo) {
    this.resInfo = resInfo;
  }

  public static StrResDefault create(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    if (kind == ResInfoDetails.Kind.STRING) {
      return new StrResDefault(resInfo);
    } else {
      throw new TyResException(
        "String resource is not compatible with " +
        kind +
        ". Must be of kind " +
        ResInfoDetails.Kind.STRING +
        "'."
      );
    }
  }

  @Override
  public String toString() {
    return "StrRes{" + "info=" + info() + '}';
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other instanceof MsgResDefault) {
      var castOther = (MsgResDefault) other;
      return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    result = 31 * result + StrResDefault.class.hashCode();
    return result;
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
  public StrResDefault withArgs(Object[] args) {
    if (args.length > 0) {
      throw new TyResException("String resources cannot have an argument.");
    }
    return this;
  }
}
