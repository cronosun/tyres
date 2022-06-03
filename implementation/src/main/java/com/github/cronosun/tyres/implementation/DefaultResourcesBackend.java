package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;
import java.io.InputStream;
import java.util.HashSet;
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
  private final Once<EffectiveNameGenerator> effectiveNameGenerator;

  public DefaultResourcesBackend(
    TextBackend textBackend,
    BinBackend binBackend,
    ArgsResolver argsResolver,
    FallbackGenerator fallbackGenerator,
    ValidatorBackend validatorBackend,
    Once<EffectiveNameGenerator> effectiveNameGenerator
  ) {
    this.textBackend = textBackend;
    this.binBackend = binBackend;
    this.argsResolver = argsResolver;
    this.fallbackGenerator = fallbackGenerator;
    this.validatorBackend = validatorBackend;
    this.effectiveNameGenerator = effectiveNameGenerator;
  }

  @Override
  public @Nullable String getText(
    Resources resources,
    EntryInfo.TextEntry entry,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  ) {
    var notFoundConfigWithNullNoDefault = notFoundConfig.withNullNoDefault(
      resources.defaultNotFoundConfig()
    );
    var localeToUse = locale(locale, resources);
    if (localeToUse == null) {
      return handleReturnForText(null, entry, null, NO_ARGS, notFoundConfigWithNullNoDefault);
    }
    var bundleInfo = entry.bundleInfo();
    validatorBackend.validate(ValidatorBackend.When.ON_USE, bundleInfo.bundleClass(), localeToUse);
    var baseName = DefaultImplementationDataProvider.baseNameForText(bundleInfo);
    var name = DefaultImplementationDataProvider.name(entry);
    var value = this.textBackend.maybeText(entry, baseName, name, localeToUse);
    return handleReturnForText(
      value,
      entry,
      localeToUse,
      NO_ARGS,
      notFoundConfigWithNullNoDefault
    );
  }

  @Override
  public @Nullable String getFmt(
    Resources resources,
    EntryInfo.TextEntry entry,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  ) {
    var notFoundConfigWithNullNoDefault = notFoundConfig.withNullNoDefault(
      resources.defaultNotFoundConfig()
    );
    var localeToUse = locale(locale, resources);
    if (localeToUse == null) {
      return handleReturnForText(null, entry, null, args, notFoundConfigWithNullNoDefault);
    }
    var resolvedArgs = argsResolver.resolve(
      resources,
      localeToUse,
      notFoundConfigWithNullNoDefault,
      args
    );
    if (resolvedArgs == null) {
      return handleReturnForText(null, entry, localeToUse, args, notFoundConfigWithNullNoDefault);
    }
    var bundleInfo = entry.bundleInfo();
    var baseName = DefaultImplementationDataProvider.baseNameForText(bundleInfo);
    var name = DefaultImplementationDataProvider.name(entry);
    var value = this.textBackend.maybeFmt(entry, baseName, name, resolvedArgs, localeToUse);
    validatorBackend.validate(
      ValidatorBackend.When.ON_USE,
      entry.bundleInfo().bundleClass(),
      localeToUse
    );
    return handleReturnForText(
      value,
      entry,
      localeToUse,
      resolvedArgs,
      notFoundConfigWithNullNoDefault
    );
  }

  @Override
  public @Nullable InputStream getInputStream(
    Resources resources,
    EntryInfo.BinEntry entry,
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
        entry.bundleInfo().bundleClass(),
        localeToUse
      );
      var baseName = DefaultImplementationDataProvider.baseNameForBin(entry.bundleInfo());
      var filename = DefaultImplementationDataProvider.filename(entry);
      inputStream = this.binBackend.maybeBin(entry, baseName, filename, localeToUse);
    }
    if (inputStream == null && required) {
      return throwNotFound(entry, localeToUse);
    } else {
      return inputStream;
    }
  }

  @Override
  public void validateAllResourcesFromBundle(Supplier<Stream<EntryInfo>> resInfo, Locale locale) {
    // first make sure everything is here
    var resInfoStream = resInfo.get();
    resInfoStream.forEach(item -> this.validateSingleResource(item, locale));
    // now ask the text backend whether there are superfluous resources
    var textEntries = resInfo
      .get()
      .filter(info -> info instanceof EntryInfo.TextEntry)
      .map(info -> (EntryInfo.TextEntry) info);
    validateNoSuperfluousResources(textEntries, locale);
  }

  private void validateNoSuperfluousResources(
    Stream<EntryInfo.TextEntry> textEntries,
    Locale locale
  ) {
    var declaredNamesInBundle = new HashSet<String>();
    var textEntriesIterator = textEntries.iterator();
    BaseName uniqueEffectiveBaseName = null;
    BaseName uniqueDeclaredBaseName = null;
    while (textEntriesIterator.hasNext()) {
      var textEntry = textEntriesIterator.next();
      var bundleInfo = textEntry.bundleInfo();
      var thisBaseName = DefaultImplementationDataProvider.baseNameForText(bundleInfo);
      if (uniqueEffectiveBaseName == null) {
        uniqueEffectiveBaseName = thisBaseName;
      } else {
        if (!uniqueEffectiveBaseName.equals(thisBaseName)) {
          // cannot validate if there are multiple base names in one bundle
          return;
        }
      }
      if (uniqueDeclaredBaseName == null) {
        uniqueDeclaredBaseName = bundleInfo.baseName();
      } else {
        if (!uniqueDeclaredBaseName.equals(bundleInfo.baseName())) {
          // cannot validate if there are multiple base names in one bundle
          return;
        }
      }
      declaredNamesInBundle.add(textEntry.name());
    }
    if (uniqueEffectiveBaseName == null) {
      return;
    }
    if (uniqueDeclaredBaseName == null) {
      return;
    }

    // ask the backend what resources we have
    var originalNamesInBundle = textBackend.maybeAllResourcesInBundle(
      uniqueEffectiveBaseName,
      locale
    );
    if (originalNamesInBundle == null) {
      return;
    }
    var originalNamesInBundleIterator = originalNamesInBundle.iterator();
    var effectiveNameGenerator = this.effectiveNameGenerator.get();
    while (originalNamesInBundleIterator.hasNext()) {
      var effectiveNameInBundle = originalNamesInBundleIterator.next();
      var declaredName = effectiveNameGenerator.fromEffectiveNameToDeclaredName(
        uniqueDeclaredBaseName,
        uniqueEffectiveBaseName,
        effectiveNameInBundle
      );
      if (declaredName != null) {
        if (!declaredNamesInBundle.contains(declaredName)) {
          throw new TyResException(
            "There's text/pattern found, key '" +
            declaredName +
            "', " +
            uniqueDeclaredBaseName.conciseDebugString() +
            " (locale '" +
            locale.toLanguageTag() +
            "') but it's not used in the bundle interface (effective base name " +
            uniqueEffectiveBaseName.conciseDebugString() +
            "). Remove the text/pattern, or use it in the bundle interface!"
          );
        }
      }
    }
  }

  private void validateSingleResource(EntryInfo entry, Locale locale) {
    if (entry instanceof EntryInfo.TextEntry) {
      var cast = (EntryInfo.TextEntry) entry;
      var baseName = DefaultImplementationDataProvider.baseNameForText(entry.bundleInfo());
      var name = DefaultImplementationDataProvider.name(cast);
      switch (cast.type()) {
        case TEXT:
          textBackend.validateText(cast, baseName, name, locale);
          break;
        case FMT:
          textBackend.validateFmt(cast, baseName, name, locale);
          break;
        default:
          throw new TyResException("Unknown text type: " + cast.type());
      }
    } else if (entry instanceof EntryInfo.BinEntry) {
      var cast = (EntryInfo.BinEntry) entry;
      var baseName = DefaultImplementationDataProvider.baseNameForBin(entry.bundleInfo());
      var filename = DefaultImplementationDataProvider.filename(cast);
      this.binBackend.validate(cast, baseName, filename, locale);
    }
  }

  private String handleReturnForText(
    @Nullable String text,
    EntryInfo.TextEntry info,
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

  private <T> T throwNotFound(EntryInfo entryInfo, @Nullable Locale locale) {
    if (locale == null) {
      var debugString = entryInfo.conciseDebugString();
      throw new TyResException("No locale found resolving '" + debugString + "'.");
    } else {
      var debugString = WithConciseDebugString.build(List.of(locale.toLanguageTag(), entryInfo));
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
