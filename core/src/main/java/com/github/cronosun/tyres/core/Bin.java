package com.github.cronosun.tyres.core;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/**
 * Binary resource.
 */
public interface Bin extends WithConciseDebugString {
  @Nullable
  InputStream getInputStream(@Nullable Locale locale, boolean required);

  default InputStream get(Locale locale) {
    var localeNotNull = Objects.requireNonNull(locale, "Locale is null");
    var result = getInputStream(localeNotNull, true);
    return Objects.requireNonNull(
      result,
      "Implementation returned a null input stream (and required is true). This is an implementation error."
    );
  }

  default InputStream get() {
    var result = getInputStream(null, true);
    return Objects.requireNonNull(
      result,
      "Implementation returned a null input stream (and required is true). This is an implementation error."
    );
  }

  @Nullable
  default InputStream maybe(Locale locale) {
    var localeNotNull = Objects.requireNonNull(locale, "Locale is null");
    return getInputStream(localeNotNull, false);
  }

  @Nullable
  default InputStream maybe() {
    return getInputStream(null, false);
  }
}
