package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.spi.ResourceBundleProvider;

final class ResourceBundleTextBackend implements TextBackend {

    private static final Logger LOGGER = Logger.getLogger(ResourceBundleTextBackend.class.getName());
    private final MessageFormatBackend messageFormatBackend;
    @Nullable
    private final ResourceBundleProvider resourceBundleProvider;

    @Override
    public @Nullable String maybeFmt(ResInfo.Text info, Object[] args, Locale locale) {
        var pattern = maybeText(info, locale);
        if (pattern!=null) {

        } else {
            return null;
        }
    }

    @Override
    public @Nullable String maybeText(ResInfo.Text info, Locale locale) {
        var bundle = getResourceBundleForMessages(info.bundleInfo(), locale);
        var string = getString(bundle, info);
        if (string == null) {
            // try the default
            return info.defaultValue();
        } else {
            return string;
        }
    }

    @Nullable
    private String getString(@Nullable ResourceBundle bundle, ResInfo.Text info) {
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
