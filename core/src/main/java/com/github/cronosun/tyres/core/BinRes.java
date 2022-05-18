package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

@ThreadSafe
public final class BinRes implements Res<BinRes, BinMarker> {

  private static final Object[] NO_ARGS = new Object[] {};
  private final ResInfo resInfo;

  private BinRes(ResInfo resInfo) {
    this.resInfo = resInfo;
  }

  public static BinRes create(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    if (kind == ResInfoDetails.Kind.BINARY) {
      return new BinRes(resInfo);
    } else {
      throw new TyResException(
        "Binary resource is not compatible with " +
        kind +
        ". Must be of kind " +
        ResInfoDetails.Kind.BINARY +
        "'."
      );
    }
  }

  @Override
  public String toString() {
    return "BinRes{" + "info=" + info() + '}';
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other instanceof BinRes) {
      var castOther = (BinRes) other;
      return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(info());
    result = 31 * result + Arrays.hashCode(args());
    result = 31 * result + BinRes.class.hashCode();
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
  public BinRes withArgs(Object[] args) {
    if (args.length > 0) {
      throw new TyResException("Binary resources cannot have an argument.");
    }
    return this;
  }
}
