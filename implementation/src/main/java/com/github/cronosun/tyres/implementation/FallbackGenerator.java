package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface FallbackGenerator {
  /**
   * The default fallback generator. This is the right choice for most cases.
   */
  static FallbackGenerator defaultInstance() {
    return DefaultFallbackGenerator.instance();
  }

  String fallbackMsgFor(EntryInfo entryInfo, @Nullable Locale locale, Object[] args);
}
