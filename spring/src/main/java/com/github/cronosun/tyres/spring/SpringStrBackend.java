package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.StrBackend;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public final class SpringStrBackend implements StrBackend {

  private final MessageSourceProvider messageSourceProvider;

  public SpringStrBackend(MessageSourceProvider messageSourceProvider) {
    this.messageSourceProvider = messageSourceProvider;
  }

  @Override
  public @Nullable String maybeMessage(ResInfo resInfo, Object[] args, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var source = messageSourceProvider.messageSource(bundleInfo, locale);
    var name = resInfo.details().asStringResource().name();
    try {
      return source.getMessage(name, args, null, locale);
    } catch (IllegalArgumentException iae) {
      var bundleRef = resInfo.bundle().baseName().value();
      throw new TyResException(
        "Invalid format / cannot parse: '" +
        name +
        "' (locale " +
        locale +
        ") in bundle " +
        bundleRef +
        "'.",
        iae
      );
    }
  }

  @Override
  public @Nullable String maybeString(ResInfo resInfo, Locale locale) {
    return maybeMessage(resInfo, null, locale);
  }

  @Override
  public @Nullable Set<String> resourceNamesInBundleForValidation(
    BundleInfo bundleInfo,
    Locale locale
  ) {
    var source = messageSourceProvider.messageSource(bundleInfo, locale);
    return source.resourceNamesInBundleForValidation(bundleInfo, locale);
  }

}
