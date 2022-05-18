package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Res;
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

  @Nullable
  private final ResourceBundleProvider resourceBundleProvider;

  private static final DefaultStringBackend INSTANCE = new DefaultStringBackend(
    null,
    MessageFormatter.defaultImplementaion()
  );
  private static final Logger LOGGER = Logger.getLogger(DefaultStringBackend.class.getName());
  private final MessageFormatter messageFormatter;

  private DefaultStringBackend(
    @Nullable ResourceBundleProvider resourceBundleProvider,
    @Nullable MessageFormatter messageFormatter
  ) {
    this.resourceBundleProvider = resourceBundleProvider;
    this.messageFormatter =
      Objects.requireNonNullElse(messageFormatter, MessageFormatter.defaultImplementaion());
  }

  public DefaultStringBackend withResourceBundleProvider(
    ResourceBundleProvider resourceBundleProvider
  ) {
    return new DefaultStringBackend(resourceBundleProvider, this.messageFormatter);
  }

  public DefaultStringBackend withMessageFormatter(MessageFormatter messageFormatter) {
    return new DefaultStringBackend(this.resourceBundleProvider, messageFormatter);
  }

  public static DefaultStringBackend instance() {
    return INSTANCE;
  }

  @Nullable
  @Override
  public String maybeMessage(Res<?> resource, Object[] args, Locale locale, boolean throwOnError) {
    var pattern = getStringOrDefault(resource, locale, throwOnError);
    if (pattern != null) {
      return messageFormatter.format(pattern, args, locale, throwOnError);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable String maybeString(Res<?> resource, Locale locale, boolean throwOnError) {
    return getStringOrDefault(resource, locale, throwOnError);
  }

  @Nullable
  private String getStringOrDefault(Res<?> resource, Locale locale, boolean throwOnError) {
    if (!isCorrectResourceType(resource, throwOnError)) {
      return null;
    }
    var bundle = getResourceBundleForMessages(resource, locale);
    var string = getString(bundle, resource);
    if (string == null) {
      // try the default
      return resource.info().details().asStringResouce().defaultValue();
    } else {
      return string;
    }
  }

  private boolean isCorrectResourceType(Res<?> resource, boolean throwOnError) {
    var kind = resource.info().details().kind();
    var correctType = kind == ResInfoDetails.Kind.STRING;
    if (throwOnError && !correctType) {
      throw new TyResException(
        "Invalid resource kind (must be a string resource). It's " +
        kind +
        ". Resource '" +
        resource +
        "'."
      );
    }
    return correctType;
  }

  @Nullable
  private String getString(@Nullable ResourceBundle bundle, Res<?> resource) {
    if (bundle != null) {
      var resInfo = resource.info();
      var key = resInfo.details().asStringResouce().name();
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
  private ResourceBundle getResourceBundleForMessages(Res<?> resource, Locale locale) {
    var bundleInfo = resource.info().bundle();
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
