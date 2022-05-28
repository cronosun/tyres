package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BaseName;
import java.util.Locale;

public interface MessageSourceProvider {
  static MessageSourceProvider cached(MessageSourceFactory factory) {
    return new CachedMessageSourceProvider(factory);
  }

  ExtMessageSource messageSource(BaseName baseName, Locale locale);
}
