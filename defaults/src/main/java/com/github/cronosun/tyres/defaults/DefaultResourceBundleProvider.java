package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

public class DefaultResourceBundleProvider implements ResourceBundleProvider {

  private static final DefaultResourceBundleProvider INSTANCE = new DefaultResourceBundleProvider();

  public static DefaultResourceBundleProvider instance() {
    return INSTANCE;
  }

  public DefaultResourceBundleProvider() {}

  private static final Logger LOGGER = Logger.getLogger(
    DefaultResourceBundleProvider.class.getName()
  );

  @Override
  public TyResResourceBundle getBundleFor(ResInfo resInfo) {
    // TODO: Cache?
    var bundleInfo = resInfo.bundle();
    return new DefaultTyResResourceBundle(bundleInfo);
  }

  private static final class DefaultTyResResourceBundle implements TyResResourceBundle {

    private final BundleInfo bundleInfo;

    private DefaultTyResResourceBundle(BundleInfo bundleInfo) {
      this.bundleInfo = bundleInfo;
    }

    @Nullable
    @Override
    public String getString(ResInfo resInfo, Locale locale) {
      var resourceBundle = getResourceBundleForMessages(locale);
      if (resourceBundle != null) {
        var key = resInfo.name();
        if (resourceBundle.containsKey(key)) {
          return resourceBundle.getString(key);
        }
        return null;
      } else {
        return null;
      }
    }

    @Override
    public String debugReference() {
      var baseName = bundleInfo.baseName().value();
      return "{" + baseName + "}";
    }

    @Nullable
    private ResourceBundle getResourceBundleForMessages(Locale locale) {
      var baseName = bundleInfo.baseName().value();
      try {
        return ResourceBundle.getBundle(baseName, locale);
      } catch (MissingResourceException missingResourceException) {
        LOGGER.log(Level.INFO, "Missing resource bundle", missingResourceException);
        return null;
      }
    }
  }
}
