package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ResInfoDetails;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;
import org.jetbrains.annotations.Nullable;

final class DefaultStringBackend implements StringBackend {

  private static final DefaultStringBackend INSTANCE = new DefaultStringBackend(
    null,
    MessageFormatter.defaultImplementation()
  );
  private static final Logger LOGGER = Logger.getLogger(DefaultStringBackend.class.getName());

  @Nullable
  private final ResourceBundleProvider resourceBundleProvider;

  private final MessageFormatter messageFormatter;

  private DefaultStringBackend(
    @Nullable ResourceBundleProvider resourceBundleProvider,
    @Nullable MessageFormatter messageFormatter
  ) {
    this.resourceBundleProvider = resourceBundleProvider;
    this.messageFormatter =
      Objects.requireNonNullElse(messageFormatter, MessageFormatter.defaultImplementation());
  }

  public static DefaultStringBackend instance() {
    return INSTANCE;
  }

  public DefaultStringBackend withResourceBundleProvider(
    ResourceBundleProvider resourceBundleProvider
  ) {
    return new DefaultStringBackend(resourceBundleProvider, this.messageFormatter);
  }

  public DefaultStringBackend withMessageFormatter(MessageFormatter messageFormatter) {
    return new DefaultStringBackend(this.resourceBundleProvider, messageFormatter);
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

  @Nullable
  private String getStringOrDefault(ResInfo resInfo, Locale locale) {
    assertCorrectResourceKind(resInfo);
    var bundle = getResourceBundleForMessages(resInfo, locale);
    var string = getString(bundle, resInfo);
    if (string == null) {
      // try the default
      return resInfo.details().asStringResource().defaultValue();
    } else {
      return string;
    }
  }

  private void assertCorrectResourceKind(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    var correctType = kind == ResInfoDetails.Kind.STRING;
    if (!correctType) {
      throw new TyResException(
        "Invalid resource kind (must be a string resource). It's " +
        kind +
        ". Resource '" +
        resInfo.debugReference() +
        "'."
      );
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
  private ResourceBundle getResourceBundleForMessages(ResInfo resInfo, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var baseName = bundleInfo.baseName().value();
    try {
      return ResourceBundle.getBundle(baseName, locale);
    } catch (MissingResourceException missingResourceException) {
      LOGGER.log(Level.INFO, "Missing resource bundle", missingResourceException);
      return null;
    }
  }

  private ResourceBundle getBundle(String baseName, Locale locale) {
    var resourceBundleProvider = this.resourceBundleProvider;
    if (resourceBundleProvider == null) {
      try {
        return ResourceBundle.getBundle(baseName, locale);
      } catch (MissingResourceException missingResourceException) {
        LOGGER.log(Level.INFO, "Missing resource bundle", missingResourceException);
        return null;
      }
    } else {
      return resourceBundleProvider.getBundle(baseName, locale);
    }
  }
}
