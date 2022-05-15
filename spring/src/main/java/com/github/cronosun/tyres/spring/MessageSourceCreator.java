package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.springframework.context.MessageSource;

@ThreadSafe
public interface MessageSourceCreator {
  CreatedMessageSource createMessageSource(ResInfo resInfo, Locale locale);

  Object cacheKeyFor(ResInfo resInfo, Locale locale);

  interface CreatedMessageSource {
    MessageSource messageSource();
    Object cacheKey();
  }
}
