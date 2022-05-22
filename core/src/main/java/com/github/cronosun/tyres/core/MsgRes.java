package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * A formatted resource with 0-n arguments.
 *
 * @see StrRes (similar but not formatted).
 */
public interface MsgRes extends Res<MsgRes>, Resolvable {
  static MsgRes create(ResInfo resInfo) {
    return MsgResDefault.create(resInfo);
  }

  @Override
  default String get(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return resources.msg().get(this, notFoundStrategy, locale);
  }

  @Override
  default @Nullable String maybe(Resources resources, Locale locale) {
    return resources.msg().maybe(this, locale);
  }
}
