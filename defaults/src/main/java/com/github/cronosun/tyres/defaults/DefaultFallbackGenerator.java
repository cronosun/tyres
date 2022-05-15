package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import java.util.Arrays;

public final class DefaultFallbackGenerator implements FallbackGenerator {

  private static final DefaultFallbackGenerator INSTANCE = new DefaultFallbackGenerator();

  public static DefaultFallbackGenerator instance() {
    return INSTANCE;
  }

  private DefaultFallbackGenerator() {}

  @Override
  public String generateFallbackMessageFor(ResInfo resInfo, Object[] args) {
    var debugReference = resInfo.debugReference();
    if (args.length == 0) {
      return debugReference;
    } else {
      return "{" + debugReference + " " + Arrays.toString(args) + "}";
    }
  }
}
