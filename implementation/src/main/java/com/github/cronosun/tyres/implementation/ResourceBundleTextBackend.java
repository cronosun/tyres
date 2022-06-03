package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.Validation;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

final class ResourceBundleTextBackend implements TextBackend {

  private static final Logger LOGGER = Logger.getLogger(ResourceBundleTextBackend.class.getName());
  private final MessageFormatter messageFormatter;

  @Nullable
  private final ResourceBundleProvider resourceBundleProvider;

  ResourceBundleTextBackend(
    MessageFormatter messageFormatter,
    @Nullable ResourceBundleProvider resourceBundleProvider
  ) {
    this.messageFormatter = messageFormatter;
    this.resourceBundleProvider = resourceBundleProvider;
  }

  @Override
  public @Nullable String maybeFmt(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Object[] args,
    Locale locale
  ) {
    var pattern = maybeText(entry, baseName, name, locale);
    if (pattern != null) {
      return this.messageFormatter.format(pattern, args, locale);
    } else {
      return null;
    }
  }

  @Override
  public void validateFmt(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var pattern = maybeText(entry, baseName, name, locale);
    if (pattern != null) {
      var numberOfArguments = entry.method().getParameterCount();
      this.messageFormatter.validatePattern(pattern, locale, numberOfArguments);
    } else {
      if (entry.required()) {
        throw new TyResException(
          "Text (fmt) " +
          entry.conciseDebugString() +
          " for locale '" +
          locale.toLanguageTag() +
          "' not found and it's not marked as optional (see @" +
          Validation.class.getSimpleName() +
          "' annotation)."
        );
      }
    }
  }

  @Override
  public @Nullable String maybeText(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var bundle = getBundle(baseName, locale);
    var text = getString(bundle, name);
    if (text != null) {
      return text;
    } else {
      // try the default
      return entry.defaultValue();
    }
  }

  @Override
  public void validateText(
    EntryInfo.TextEntry entry,
    BaseName baseName,
    String name,
    Locale locale
  ) {
    var text = maybeText(entry, baseName, name, locale);
    if (text == null && entry.required()) {
      throw new TyResException(
        "Text " +
        entry.conciseDebugString() +
        " for locale '" +
        locale.toLanguageTag() +
        "' not found and it's not marked as optional (see @" +
        Validation.class.getSimpleName() +
        "' annotation)."
      );
    }
  }

  @Override
  public Stream<String> maybeAllResourcesInBundle(BaseName baseName, Locale locale) {
    var bundle = getBundle(baseName, locale);
    if (bundle != null) {
      var entries = bundle.getKeys();
      return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(entries.asIterator(), Spliterator.ORDERED),
        false
      );
    } else {
      return Stream.empty();
    }
  }

  @Nullable
  private String getString(@Nullable ResourceBundle bundle, String name) {
    if (bundle != null) {
      if (bundle.containsKey(name)) {
        return bundle.getString(name);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Nullable
  private ResourceBundle getBundle(BaseName baseName, Locale locale) {
    var baseNameValue = baseName.value();
    var resourceBundleProvider = this.resourceBundleProvider;
    if (resourceBundleProvider == null) {
      try {
        // From Java 9 onwards property files are encoded as UTF-8 by default
        return ResourceBundle.getBundle(
          baseNameValue,
          locale,
          ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
        );
      } catch (MissingResourceException missingResourceException) {
        LOGGER.log(Level.FINE, "Missing resource bundle", missingResourceException);
        return null;
      }
    } else {
      return resourceBundleProvider.getBundle(baseNameValue, locale);
    }
  }
}
