package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import java.util.Arrays;

final class DefaultFallbackGenerator implements FallbackGenerator {

  private static final DefaultFallbackGenerator INSTANCE = new DefaultFallbackGenerator();

  private DefaultFallbackGenerator() {}

  public static DefaultFallbackGenerator instance() {
    return INSTANCE;
  }

  @Override
  public String generateFallbackMessageFor(ResInfo resInfo, Object[] args) {
    var conciseDebugString = resInfo.conciseDebugString();
    if (args.length == 0) {
      return conciseDebugString;
    } else {
      return "{" + conciseDebugString + " " + Arrays.toString(args) + "}";
    }
  }
}
