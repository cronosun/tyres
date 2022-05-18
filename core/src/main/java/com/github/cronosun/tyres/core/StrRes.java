package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
public final class StrRes implements Res<StrRes, StrMarker> {

  private static final Object[] NO_ARGS = new Object[] {};
  private final ResInfo resInfo;

  private StrRes(ResInfo resInfo) {
    this.resInfo = resInfo;
  }

  public static StrRes create(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    if (kind == ResInfoDetails.Kind.STRING) {
      return new StrRes(resInfo);
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
    if (other instanceof MsgRes) {
      var castOther = (MsgRes) other;
      return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    result = 31 * result + StrRes.class.hashCode();
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
  public StrRes withArgs(Object[] args) {
    if (args.length > 0) {
      throw new TyResException("String resources cannot have an argument.");
    }
    return this;
  }
}
