package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;

@ThreadSafe
public interface MessageSourceFactory {
  CreatedMessageSource createMessageSource(BundleInfo bundleInfo, Locale locale);

  Object cacheKeyFor(BundleInfo bundleInfo, Locale locale);

  interface CreatedMessageSource {
    ExtMessageSource messageSource();

    Object cacheKey();
  }

  static MessageSourceFactory resourceBundle() {
    return new ResourceBundleMessageSourceFactory();
  }
}
