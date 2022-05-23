package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import java.util.Locale;

public interface MessageSourceProvider {
  ExtMessageSource messageSource(BundleInfo bundleInfo, Locale locale);

  static MessageSourceProvider cached(MessageSourceFactory factory) {
    return new CachedMessageSourceProvider(factory);
  }
}
