package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.defaults.validation.ValidationError;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultMsgStrBackend implements MsgStrBackend {

  private static final DefaultMsgStrBackend INSTANCE = new DefaultMsgStrBackend(
    null,
    MessageFormatter.defaultImplementation()
  );
  private static final Logger LOGGER = Logger.getLogger(DefaultMsgStrBackend.class.getName());

  @Nullable
  private final ResourceBundleProvider resourceBundleProvider;

  private final MessageFormatter messageFormatter;

  private DefaultMsgStrBackend(
    @Nullable ResourceBundleProvider resourceBundleProvider,
    @Nullable MessageFormatter messageFormatter
  ) {
    this.resourceBundleProvider = resourceBundleProvider;
    this.messageFormatter =
      Objects.requireNonNullElse(messageFormatter, MessageFormatter.defaultImplementation());
  }

  public static DefaultMsgStrBackend instance() {
    return INSTANCE;
  }

  @Nullable
  @Override
  public String maybeMessage(ResInfo.Str resInfo, Object[] args, Locale locale) {
    var pattern = getStringOrDefault(resInfo, locale);
    if (pattern != null) {
      return messageFormatter.format(pattern, args, locale);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable String maybeString(ResInfo.Str resInfo, Locale locale) {
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

  @Override
  public @Nullable ValidationError validateMessage(
    ResInfo.Str resInfo,
    int numberOfArguments,
    Locale locale,
    boolean optional
  ) {
    var pattern = maybeString(resInfo, locale);
    if (pattern != null) {
      return messageFormatter.validateMessage(resInfo, pattern, numberOfArguments, locale);
    } else {
      if (!optional) {
        return new ValidationError.ResourceNotFound(resInfo, locale);
      } else {
        // no problem, resource is optional.
        return null;
      }
    }
  }

  @Nullable
  private String getStringOrDefault(ResInfo.Str resInfo, Locale locale) {
    var bundle = getResourceBundleForMessages(resInfo.bundle(), locale);
    var string = getString(bundle, resInfo);
    if (string == null) {
      // try the default
      return resInfo.defaultValue();
    } else {
      return string;
    }
  }

  @Nullable
  private String getString(@Nullable ResourceBundle bundle, ResInfo.Str resInfo) {
    if (bundle != null) {
      var key = resInfo.name();
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
