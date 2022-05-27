package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.implementation.experiment.CurrentLocaleProvider;
import com.github.cronosun.tyres.implementation.experiment.DefaultResources;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class TestUtil {

  private TestUtil() {}

  public static Resources newInstance(DefaultNotFoundConfig notFoundConfig) {
    return DefaultResources.todoNewInstance(notFoundConfig);
  }

  public static Resources newInstance(
    DefaultNotFoundConfig notFoundConfig,
    CurrentLocaleProvider currentLocaleProvider
  ) {
    return DefaultResources.todoNewInstance(notFoundConfig, currentLocaleProvider);
  }

  public static String binToString(Bin bin, Locale locale) throws IOException {
    try (var inputStream = bin.get(locale)) {
      var bytes = inputStream.readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
