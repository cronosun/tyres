package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultStrBackend implements StrBackend {

  private static final DefaultStrBackend INSTANCE = new DefaultStrBackend(
    null,
    MessageFormatter.defaultImplementation()
  );
  private static final Logger LOGGER = Logger.getLogger(DefaultStrBackend.class.getName());

  @Nullable
  private final ResourceBundleProvider resourceBundleProvider;

  private final MessageFormatter messageFormatter;

  private DefaultStrBackend(
    @Nullable ResourceBundleProvider resourceBundleProvider,
    @Nullable MessageFormatter messageFormatter
  ) {
    this.resourceBundleProvider = resourceBundleProvider;
    this.messageFormatter =
      Objects.requireNonNullElse(messageFormatter, MessageFormatter.defaultImplementation());
  }

  public static DefaultStrBackend instance() {
    return INSTANCE;
  }

  @Nullable
  @Override
  public String maybeMessage(ResInfo resInfo, Object[] args, Locale locale) {
    var pattern = getStringOrDefault(resInfo, locale);
    if (pattern != null) {
      return messageFormatter.format(pattern, args, locale);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable String maybeString(ResInfo resInfo, Locale locale) {
    return getStringOrDefault(resInfo, locale);
  }

  @Override
  public Set<String> resourceNamesInBundleForValidation(BundleInfo bundleInfo, Locale locale) {
    var bundle = getResourceBundleForMessages(bundleInfo, locale);
    if (bundle == null) {
      return Set.of();
    } else {
      var keysIterator = bundle.getKeys().asIterator();
      var keyStream = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(keysIterator, Spliterator.DISTINCT),
        false
      );
      return keyStream.collect(Collectors.toUnmodifiableSet());
    }
  }

  @Nullable
  private String getStringOrDefault(ResInfo resInfo, Locale locale) {
    var bundle = getResourceBundleForMessages(resInfo.bundle(), locale);
    var string = getString(bundle, resInfo);
    if (string == null) {
      // try the default
      return resInfo.details().asStringResource().defaultValue();
    } else {
      return string;
    }
  }

  @Nullable
  private String getString(@Nullable ResourceBundle bundle, ResInfo resInfo) {
    if (bundle != null) {
      var key = resInfo.details().asStringResource().name();
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
    var baseName = bundleInfo.baseName().value();
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
