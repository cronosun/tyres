package com.github.cronosun.tyres.core;

public interface StrRes extends Res<StrRes> {
  static StrResDefault create(ResInfo resInfo) {
    return StrResDefault.create(resInfo);
  }
}
