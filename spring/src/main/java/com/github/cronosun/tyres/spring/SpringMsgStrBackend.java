package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.backends.MsgStrBackend;
import java.util.Locale;
import java.util.Set;

import com.github.cronosun.tyres.defaults.validation.ValidationError;
import org.jetbrains.annotations.Nullable;

public final class SpringMsgStrBackend implements MsgStrBackend {

  private final MessageSourceProvider messageSourceProvider;

  public SpringMsgStrBackend(MessageSourceProvider messageSourceProvider) {
    this.messageSourceProvider = messageSourceProvider;
  }

  @Override
  public @Nullable String maybeMessage(ResInfo resInfo, Object[] args, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var source = messageSourceProvider.messageSource(bundleInfo, locale);
    var name = resInfo.details().asStringResource().name();
    try {
      return source.message(name, args,  locale);
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
  public @Nullable ValidationError validateMessage(ResInfo resInfo, int numberOfArguments, Locale locale, boolean optional) {
    // TODO
    return null;
  }

  @Override
  public boolean validateStringExists(ResInfo resInfo, Locale locale) {
    // TODO
    return false;
  }

  @Override
  public @Nullable String maybeString(ResInfo resInfo, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var source = messageSourceProvider.messageSource(bundleInfo, locale);
    var name = resInfo.details().asStringResource().name();
    return source.string(name, locale);
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
