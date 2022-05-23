package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.Arrays;
import java.util.List;

@ThreadSafe
final class DefaultFallbackGenerator implements FallbackGenerator {

  private static final DefaultFallbackGenerator INSTANCE = new DefaultFallbackGenerator();

  private DefaultFallbackGenerator() {}

  public static DefaultFallbackGenerator instance() {
    return INSTANCE;
  }

  @Override
  public String generateFallbackMessageFor(ResInfo resInfo, Object[] args) {
    if (args.length == 0) {
      return resInfo.conciseDebugString();
    } else {
      return WithConciseDebugString.build(List.of(resInfo, args));
    }
  }
}
