package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@ThreadSafe
public final class DefaultMessageSourceCreator implements MessageSourceCreator {

  private final String baseNamePrefix = "classpath:";

  @Override
  public CreatedMessageSource createMessageSource(ResInfo resInfo, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var baseName = bundleInfo.baseName().value();

    var prefixedBaseName = baseNamePrefix + baseName;
    var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename(prefixedBaseName);

    return new CreatedMessageSource() {
      @Override
      public MessageSource messageSource() {
        return messageSource;
      }

      @Override
      public Object cacheKey() {
        return baseName;
      }
    };
  }

  @Override
  public Object cacheKeyFor(ResInfo resInfo, Locale locale) {
    var bundleInfo = resInfo.bundle();
    return bundleInfo.baseName().value();
  }
}
