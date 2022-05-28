package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;

@ThreadSafe
public interface MessageSourceFactory {
  static MessageSourceFactory resourceBundle() {
    return new ResourceBundleMessageSourceFactory();
  }

  CreatedMessageSource createMessageSource(BaseName baseName, Locale locale);

  Object cacheKeyFor(BaseName baseName, Locale locale);

  interface CreatedMessageSource {
    ExtMessageSource messageSource();

    Object cacheKey();
  }
}
