package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.experiment.Bin;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.Resources2;
import com.github.cronosun.tyres.implementation.experiment.DefaultResources2;
import com.github.cronosun.tyres.implementation.implementation.ResourcesConstructor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class TestUtil {

  private TestUtil() {}

  @Deprecated
  public static Resources newImplementation(MsgNotFoundStrategy notFoundStrategy) {
    return new ResourcesConstructor(notFoundStrategy).construct();
  }

  public static Resources2 newInstance(DefaultNotFoundConfig notFoundConfig) {
    return DefaultResources2.todoNewInstance(notFoundConfig);
  }

  public static String binToString(Bin bin, Locale locale) throws IOException {
    try (var inputStream = bin.get(locale)) {
      var bytes = inputStream.readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
