package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface FallbackGenerator {
  String fallbackMsgFor(ResInfo resInfo, @Nullable Locale locale, Object[] args);

  static FallbackGenerator defaultInstance() {
    return DefaultFallbackGenerator.instance();
  }
}
