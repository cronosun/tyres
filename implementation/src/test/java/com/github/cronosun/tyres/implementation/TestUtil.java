package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.experiment.Bin;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.Resources2;
import com.github.cronosun.tyres.implementation.experiment.CurrentLocaleProvider;
import com.github.cronosun.tyres.implementation.experiment.DefaultResources2;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class TestUtil {

  private TestUtil() {}

  public static Resources2 newInstance(DefaultNotFoundConfig notFoundConfig) {
    return DefaultResources2.todoNewInstance(notFoundConfig);
  }

  public static Resources2 newInstance(
    DefaultNotFoundConfig notFoundConfig,
    CurrentLocaleProvider currentLocaleProvider
  ) {
    return DefaultResources2.todoNewInstance(notFoundConfig, currentLocaleProvider);
  }

  public static String binToString(Bin bin, Locale locale) throws IOException {
    try (var inputStream = bin.get(locale)) {
      var bytes = inputStream.readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    }
  }
}
