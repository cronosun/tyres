package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class TestUtil {

  private TestUtil() {}

  public static Resources newInstance(DefaultNotFoundConfig notFoundConfig) {
    return new ResourcesBuilder().defaultNotFoundConfig(notFoundConfig).build();
  }

  public static Resources newInstanceValidateOnUse(DefaultNotFoundConfig notFoundConfig) {
    return new ResourcesBuilder()
      .defaultNotFoundConfig(notFoundConfig)
      .validateOnBundleUse(true)
      .build();
  }

  public static Resources newInstance(
    DefaultNotFoundConfig notFoundConfig,
    CurrentLocaleProvider currentLocaleProvider
  ) {
    return new ResourcesBuilder()
      .defaultNotFoundConfig(notFoundConfig)
      .currentLocaleProvider(currentLocaleProvider)
      .build();
  }

  public static String binToString(Bin bin, Locale locale) throws IOException {
    try (var inputStream = bin.get(locale)) {
      var bytes = inputStream.readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
