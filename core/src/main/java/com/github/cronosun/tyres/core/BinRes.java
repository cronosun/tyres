package com.github.cronosun.tyres.core;

public interface BinRes extends Res<BinRes> {
  static BinRes create(ResInfo resInfo) {
    return BinResDefault.create(resInfo);
  }
}
