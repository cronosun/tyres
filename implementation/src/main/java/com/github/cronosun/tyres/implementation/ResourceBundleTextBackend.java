package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

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
  public @Nullable String maybeFmt(ResInfo.TextResInfo info, Object[] args, Locale locale) {
    var pattern = maybeText(info, locale);
    if (pattern != null) {
      return this.messageFormatter.format(pattern, args, locale);
    } else {
      return null;
    }
  }

  @Override
  public void validateFmt(ResInfo.TextResInfo info, Locale locale) {
    var pattern = maybeText(info, locale);
    if (pattern != null) {
      var numberOfArguments = info.method().getParameterCount();
      this.messageFormatter.validatePattern(pattern, locale, numberOfArguments);
    } else {
      if (!info.validationOptional()) {
        throw new TyResException(
          "Text (fmt) " +
          info.conciseDebugString() +
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
  public @Nullable String maybeText(ResInfo.TextResInfo info, Locale locale) {
    var bundle = getResourceBundleForMessages(info.bundleInfo(), locale);
    var string = getString(bundle, info);
    if (string == null) {
      // try the default
      return info.defaultValue();
    } else {
      return string;
    }
  }

  @Override
  public void validateText(ResInfo.TextResInfo info, Locale locale) {
    var text = maybeText(info, locale);
    if (text == null && !info.validationOptional()) {
      throw new TyResException(
        "Text " +
        info.conciseDebugString() +
        " for locale '" +
        locale.toLanguageTag() +
        "' not found and it's not marked as optional (see @" +
        Validation.class.getSimpleName() +
        "' annotation)."
      );
    }
  }

  @Override
  public void validateNoSuperfluousResources(
    Stream<ResInfo.TextResInfo> allTextResourcesFromBundle,
    Locale locale
  ) {
    var iterator = allTextResourcesFromBundle.iterator();
    BaseName baseName = null;
    BaseName originalBasename = null;
    var usedKeys = new HashSet<String>();
    while (iterator.hasNext()) {
      var item = iterator.next();
      var bundle = item.bundleInfo();

      // note: if base name != effective base name, we can't perform the validation, since multiple bundles
      // might use the same bundle.
      if (!bundle.effectiveBaseName().equals(bundle.baseName())) {
        return;
      }

      if (baseName == null) {
        baseName = bundle.effectiveBaseName();
        originalBasename = bundle.baseName();
      } else {
        if (!baseName.equals(bundle.effectiveBaseName())) {
          var baseNames = WithConciseDebugString.build(
            List.of(baseName, bundle.effectiveBaseName())
          );
          throw new TyResException(
            "Unable to validate, there are resources in the set with different base names: '" +
            baseNames +
            "'."
          );
        }
      }
      usedKeys.add(item.effectiveName());
    }

    if (baseName != null) {
      var bundle = getBundle(baseName.value(), locale);
      if (bundle != null) {
        var allKeysFromBundle = bundle.getKeys();
        while (allKeysFromBundle.hasMoreElements()) {
          var keyFromBundle = allKeysFromBundle.nextElement();
          if (!usedKeys.contains(keyFromBundle)) {
            throw new TyResException(
              "The key '" +
              keyFromBundle +
              "' in bundle " +
              baseName.conciseDebugString() +
              " (locale '" +
              locale.toLanguageTag() +
              "') is not in use in the bunde (original base name " +
              originalBasename.conciseDebugString() +
              "). Remove it, or use it!"
            );
          }
        }
      }
    }
  }

  @Nullable
  private String getString(@Nullable ResourceBundle bundle, ResInfo.TextResInfo info) {
    if (bundle != null) {
      var key = info.effectiveName();
      if (bundle.containsKey(key)) {
        return bundle.getString(key);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Nullable
  private ResourceBundle getResourceBundleForMessages(BundleInfo bundleInfo, Locale locale) {
    var baseName = bundleInfo.effectiveBaseName().value();
    return getBundle(baseName, locale);
  }

  @Nullable
  private ResourceBundle getBundle(String baseName, Locale locale) {
    var resourceBundleProvider = this.resourceBundleProvider;
    if (resourceBundleProvider == null) {
      try {
        // From Java 9 onwards property files are encoded as UTF-8 by default
        return ResourceBundle.getBundle(
          baseName,
          locale,
          ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
        );
      } catch (MissingResourceException missingResourceException) {
        LOGGER.log(Level.FINE, "Missing resource bundle", missingResourceException);
        return null;
      }
    } else {
      return resourceBundleProvider.getBundle(baseName, locale);
    }
  }
}
