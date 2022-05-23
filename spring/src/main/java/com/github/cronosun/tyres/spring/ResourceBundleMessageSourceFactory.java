package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.support.ResourceBundleMessageSource;

@ThreadSafe
final class ResourceBundleMessageSourceFactory implements MessageSourceFactory {

  private static final String ENCODING = StandardCharsets.UTF_8.name();

  @Override
  public CreatedMessageSource createMessageSource(BundleInfo bundleInfo, Locale locale) {
    var baseName = bundleInfo.baseName().value();

    var messageSource = new ResourceBundleExtMessageSource();
    messageSource.setBasename(baseName);
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setDefaultEncoding(ENCODING);
    // this is required for the contract of ExtMessageSource
    messageSource.setAlwaysUseMessageFormat(true);

    return new CreatedMessageSource() {
      @Override
      public ExtMessageSource messageSource() {
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

  private static final class ResourceBundleExtMessageSource
    extends ResourceBundleMessageSource
    implements ExtMessageSource {

    @Override
    public @Nullable String message(String code, Object[] args, Locale locale) {
      return getMessage(code, args, null, locale);
    }

    @Override
    public @Nullable String string(String code, Locale locale) {
      return resolveCodeWithoutArguments(code, locale);
    }

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
