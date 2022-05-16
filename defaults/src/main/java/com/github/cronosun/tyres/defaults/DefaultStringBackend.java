package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.ResInfoDetails;
import com.github.cronosun.tyres.core.TyResException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

final class DefaultStringBackend implements StringBackend {

  private static final DefaultStringBackend INSTANCE = new DefaultStringBackend();
  private static final Logger LOGGER = Logger.getLogger(DefaultStringBackend.class.getName());

  private DefaultStringBackend() {}

  public static DefaultStringBackend instance() {
    return INSTANCE;
  }

  @Nullable
  @Override
  public String maybeMessage(Res<?> resource, Object[] args, Locale locale, boolean throwOnError) {
    if (!isCorrectResourceType(resource, throwOnError)) {
      return null;
    }
    var bundle = getResourceBundleForMessages(resource, locale);
    var string = getString(bundle, resource);
    if (string != null) {
      try {
        var format = new MessageFormat(string, locale);
        return format.format(args);
      } catch (IllegalArgumentException iae) {
        if (throwOnError) {
          throw new TyResException(
            "Invalid format / cannot parse: '" + string + "' (locale " + locale + ").",
            iae
          );
        } else {
          LOGGER.log(Level.INFO, "Invalid format", iae);
          return null;
        }
      }
    } else {
      return null;
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
}
