package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Some default implementations for the resource types, such as {@link MsgRes}.
 */
final class ResDefaults {

  private ResDefaults() {}

  @ThreadSafe
  abstract static class MsgResDefault implements Res<MsgRes>, MsgRes {

    private MsgResDefault() {}

    public static MsgResDefault create(ResInfo resInfo) {
      if (resInfo instanceof ResInfo.Str) {
        return new NoArgs((ResInfo.Str) resInfo);
      } else {
        throw new TyResException("Message resource is not compatible with " + resInfo);
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
      private final ResInfo.Str info;

      private NoArgs(ResInfo.Str info) {
        this.info = info;
      }

      @Override
      public ResInfo.Str info() {
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

      private final ResInfo.Str info;
      private final Object[] args;

      private WithArgs(ResInfo.Str info, Object[] args) {
        this.info = info;
        this.args = args;
      }

      @Override
      public ResInfo.Str info() {
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

  @ThreadSafe
  static final class StrResDefault implements Res<StrRes>, StrRes {

    private static final Object[] NO_ARGS = new Object[] {};
    private final ResInfo.Str resInfo;

    private StrResDefault(ResInfo.Str resInfo) {
      this.resInfo = resInfo;
    }

    public static StrResDefault create(ResInfo resInfo) {
      if (resInfo instanceof ResInfo.Str) {
        return new StrResDefault((ResInfo.Str) resInfo);
      } else {
        throw new TyResException("String resource is not compatible with " + resInfo);
      }
    }

    @Override
    public String toString() {
      return "StrRes{" + "info=" + info() + '}';
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) return true;
      if (other instanceof StrResDefault) {
        var castOther = (StrResDefault) other;
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
    public ResInfo.Str info() {
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

  @ThreadSafe
  static final class BinResDefault implements Res<BinRes>, BinRes {

    private static final Object[] NO_ARGS = new Object[] {};
    private final ResInfo.Bin resInfo;

    private BinResDefault(ResInfo.Bin resInfo) {
      this.resInfo = resInfo;
    }

    public static BinResDefault create(ResInfo resInfo) {
      if (resInfo instanceof ResInfo.Bin) {
        return new BinResDefault((ResInfo.Bin) resInfo);
      } else {
        throw new TyResException("Binary resource is not compatible with " + resInfo);
      }
    }

    @Override
    public String toString() {
      return "BinRes{" + "info=" + info() + '}';
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) return true;
      if (other instanceof BinResDefault) {
        var castOther = (BinResDefault) other;
        return info().equals(castOther.info()) && Arrays.equals(args(), castOther.args());
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(info());
      result = 31 * result + Arrays.hashCode(args());
      result = 31 * result + BinResDefault.class.hashCode();
      return result;
    }

    @Override
    public ResInfo.Bin info() {
      return resInfo;
    }

    @Override
    public Object[] args() {
      return NO_ARGS;
    }

    @Override
    public BinResDefault withArgs(Object[] args) {
      if (args.length > 0) {
        throw new TyResException("Binary resources cannot have an argument.");
      }
      return this;
    }
  }
}
