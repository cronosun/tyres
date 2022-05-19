package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface MsgRes extends Res<MsgRes>, Msg {
  static MsgRes create(ResInfo resInfo) {
    return MsgResDefault.create(resInfo);
  }

  @Override
  default String msg(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return resources.msg(this, notFoundStrategy, locale);
  }

  @Override
  default @Nullable String maybeMsg(Resources resources, Locale locale) {
    return resources.maybeMsg(this, locale);
  }
}
