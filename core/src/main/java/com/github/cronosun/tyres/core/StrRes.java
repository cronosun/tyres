package com.github.cronosun.tyres.core;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link StrRes} is basically the same as {@link MsgRes} with two differences:
 * <p>
 * <ul>
 *     <li>{@link StrRes} never has arguments.</li>
 *     <li>{@link StrRes} is not formatted. It's just a plain string; this also means that the pattern is not
 *     validated (since the string has no pattern).</li>
 * </ul>
 * If you write an API that needs to support both ({@link MsgRes} and {@link StrRes}), use {@link Resolvable}.
 */
public interface StrRes extends Res<StrRes>, Resolvable {
  static StrRes create(ResInfo resInfo) {
    return ResDefaults.StrResDefault.create(resInfo);
  }

  @Override
  default String get(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    return resources.str().get(this, notFoundStrategy, locale);
  }

  @Override
  @Nullable
  default String maybe(Resources resources, Locale locale) {
    return resources.str().maybe(this, locale);
  }
}
