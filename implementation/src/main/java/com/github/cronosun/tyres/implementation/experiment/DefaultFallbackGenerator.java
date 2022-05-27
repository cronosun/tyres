package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class DefaultFallbackGenerator implements FallbackGenerator {

  private static final DefaultFallbackGenerator INSTANCE = new DefaultFallbackGenerator();

  public static DefaultFallbackGenerator instance() {
    return INSTANCE;
  }

  @Override
  public String fallbackMsgFor(ResInfo resInfo, @Nullable Locale locale, Object[] args) {
    if (args.length == 0) {
      return resInfo.conciseDebugString();
    } else {
      return WithConciseDebugString.build(List.of(resInfo, args));
    }
  }
}
