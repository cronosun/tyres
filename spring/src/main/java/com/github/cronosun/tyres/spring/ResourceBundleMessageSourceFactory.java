package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.support.ResourceBundleMessageSource;

@ThreadSafe
final class ResourceBundleMessageSourceFactory implements MessageSourceFactory {

  private static final String ENCODING = StandardCharsets.UTF_8.name();

  @Override
  public CreatedMessageSource createMessageSource(BundleInfo bundleInfo, Locale locale) {
    var baseName = bundleInfo.baseName().value();

    var messageSource = new ResourceBundleWithResourceNames();
    messageSource.setBasename(baseName);
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setDefaultEncoding(ENCODING);

    return new CreatedMessageSource() {
      @Override
      public MessageSourceWithResourceNames messageSource() {
        return messageSource;
      }

      @Override
      public Object cacheKey() {
        return baseName;
      }
    };
  }

  @Override
  public Object cacheKeyFor(BundleInfo bundleInfo, Locale locale) {
    return bundleInfo.baseName().value();
  }

  private static final class ResourceBundleWithResourceNames
    extends ResourceBundleMessageSource
    implements MessageSourceWithResourceNames {

    @Override
    public Set<String> resourceNamesInBundleForValidation(BundleInfo bundleInfo, Locale locale) {
      var baseNames = this.getBasenameSet();
      var baseNamesIterator = baseNames.iterator();
      var result = new HashSet<String>();
      while (baseNamesIterator.hasNext()) {
        var basename = (String) baseNamesIterator.next();
        var bundle = this.getResourceBundle(basename, locale);
        if (bundle != null) {
          var keys = bundle.getKeys();
          while (keys.hasMoreElements()) {
            result.add(keys.nextElement());
          }
        }
      }
      return Collections.unmodifiableSet(result);
    }
  }
}
