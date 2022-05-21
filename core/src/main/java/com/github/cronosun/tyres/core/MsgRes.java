package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface MsgRes extends Res<MsgRes>, Msg {
  static MsgRes create(ResInfo resInfo) {
    return MsgResDefault.create(resInfo);
  }

  @Override
  default String msg(MsgResources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return resources.get(this, notFoundStrategy, locale);
  }

  @Override
  default @Nullable String maybeMsg(MsgResources resources, Locale locale) {
    return resources.maybe(this, locale);
  }
}
