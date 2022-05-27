package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

final class DefaultResourcesBackend implements ResourcesBackend {

  private static final Object[] NO_ARGS = new Object[] {};
  private final TextBackend textBackend;
  private final BinBackend binBackend;
  private final ArgsResolver argsResolver;
  private final FallbackGenerator fallbackGenerator;
  private final ValidatorBackend validatorBackend;

  public DefaultResourcesBackend(
    TextBackend textBackend,
    BinBackend binBackend,
    ArgsResolver argsResolver,
    FallbackGenerator fallbackGenerator,
    ValidatorBackend validatorBackend
  ) {
    this.textBackend = textBackend;
    this.binBackend = binBackend;
    this.argsResolver = argsResolver;
    this.fallbackGenerator = fallbackGenerator;
    this.validatorBackend = validatorBackend;
  }

  @Override
  public @Nullable String getText(
    Resources resources,
    ResInfo.TextResInfo info,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  ) {
    var notFoundConfigWithNullNoDefault = notFoundConfig.withNullNoDefault(
      resources.defaultNotFoundConfig()
    );
    var localeToUse = locale(locale, resources);
    if (localeToUse == null) {
      return handleReturnForText(null, info, null, NO_ARGS, notFoundConfigWithNullNoDefault);
    }
    validatorBackend.validate(
      ValidatorBackend.When.ON_USE,
      info.bundleInfo().bundleClass(),
      localeToUse
    );
    var value = this.textBackend.maybeText(info, localeToUse);
    return handleReturnForText(value, info, localeToUse, NO_ARGS, notFoundConfigWithNullNoDefault);
  }

  @Override
  public @Nullable String getFmt(
    Resources resources,
    ResInfo.TextResInfo info,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  ) {
    var notFoundConfigWithNullNoDefault = notFoundConfig.withNullNoDefault(
      resources.defaultNotFoundConfig()
    );
    var localeToUse = locale(locale, resources);
    if (localeToUse == null) {
      return handleReturnForText(null, info, null, args, notFoundConfigWithNullNoDefault);
    }
    var resolvedArgs = argsResolver.resolve(
      resources,
      localeToUse,
      notFoundConfigWithNullNoDefault,
      args
    );
    if (resolvedArgs == null) {
      return handleReturnForText(null, info, localeToUse, args, notFoundConfigWithNullNoDefault);
    }
    var value = this.textBackend.maybeFmt(info, resolvedArgs, localeToUse);
    validatorBackend.validate(
      ValidatorBackend.When.ON_USE,
      info.bundleInfo().bundleClass(),
      localeToUse
    );
    return handleReturnForText(
      value,
      info,
      localeToUse,
      resolvedArgs,
      notFoundConfigWithNullNoDefault
    );
  }

  @Override
  public @Nullable InputStream getInputStream(
    Resources resources,
    ResInfo.BinResInfo info,
    @Nullable Locale locale,
    boolean required
  ) {
    var localeToUse = locale(locale, resources);
    final InputStream inputStream;
    if (localeToUse == null) {
      inputStream = null;
    } else {
      validatorBackend.validate(
        ValidatorBackend.When.ON_USE,
        info.bundleInfo().bundleClass(),
        localeToUse
      );
      inputStream = this.binBackend.maybeBin(info, localeToUse);
    }
    if (inputStream == null && required) {
      return throwNotFound(info, localeToUse);
    } else {
      return inputStream;
    }
  }

  @Override
  public void validateAllResourcesFromBundle(Supplier<Stream<ResInfo>> resInfo, Locale locale) {
    // first make sure everything is here
    var resInfoStream = resInfo.get();
    resInfoStream.forEach(item -> this.validateSingleResouce(item, locale));
    // now ask the text backend whether there are superfluous resources
    var textResources = resInfo
      .get()
      .filter(info -> info instanceof ResInfo.TextResInfo)
      .map(info -> (ResInfo.TextResInfo) info);
    textBackend.validateNoSuperfluousResources(textResources, locale);
  }

  private void validateSingleResouce(ResInfo resInfo, Locale locale) {
    if (resInfo instanceof ResInfo.TextResInfo) {
      var cast = (ResInfo.TextResInfo) resInfo;
      switch (cast.type()) {
        case TEXT:
          textBackend.validateText(cast, locale);
          break;
        case FMT:
          textBackend.validateFmt(cast, locale);
          break;
        default:
          throw new TyResException("Unknown text type: " + cast.type());
      }
    } else if (resInfo instanceof ResInfo.BinResInfo) {
      var cast = (ResInfo.BinResInfo) resInfo;
      this.binBackend.validate(cast, locale);
    }
  }

  private String handleReturnForText(
    @Nullable String text,
    ResInfo.TextResInfo info,
    @Nullable Locale locale,
    Object[] args,
    NotFoundConfig.WithNullNoDefault notFoundConfig
  ) {
    if (text != null) {
      return text;
    }
    final DefaultNotFoundConfig defaultNotFoundConfig;
    switch (notFoundConfig) {
      case THROW:
        defaultNotFoundConfig = DefaultNotFoundConfig.THROW;
        break;
      case FALLBACK:
        defaultNotFoundConfig = DefaultNotFoundConfig.FALLBACK;
        break;
      case NULL:
        // no check required
        return null;
      default:
        throw new IllegalArgumentException("Unknown not found config: " + notFoundConfig);
    }
    switch (defaultNotFoundConfig) {
      case THROW:
        return throwNotFound(info, locale);
      case FALLBACK:
        return fallbackGenerator.fallbackMsgFor(info, locale, args);
      default:
        throw new TyResException("Unknown not found config: " + defaultNotFoundConfig);
    }
  }

  private <T> T throwNotFound(ResInfo resInfo, @Nullable Locale locale) {
    if (locale == null) {
      var debugString = resInfo.conciseDebugString();
      throw new TyResException("No locale found resolving '" + debugString + "'.");
    } else {
      var debugString = WithConciseDebugString.build(List.of(locale.toLanguageTag(), resInfo));
      throw new TyResException("Resource '" + debugString + "' not found.");
    }
  }

  @Nullable
  private Locale locale(@Nullable Locale locale, Resources resources) {
    if (locale != null) {
      return locale;
    } else {
      return resources.currentLocale();
    }
  }
}
