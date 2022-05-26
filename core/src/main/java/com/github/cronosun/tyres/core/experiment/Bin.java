package com.github.cronosun.tyres.core.experiment;

import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Binary resource.
 */
public interface Bin {
  InputStream get(@Nullable Locale locale);

  default InputStream get() {
    return get(null);
  }

  @Nullable
  InputStream maybe(@Nullable Locale locale);

  @Nullable
  default InputStream maybe() {
    return maybe(null);
  }
}
