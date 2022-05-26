package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.WithConciseDebugString;
import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.experiment.MethodInfo;
import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class DefaultResourcesBackend implements ResourcesBackend {

    private static final Object[] NO_ARGS = new Object[]{};
    private final TextBackend textBackend;
    private final FallbackGenerator fallbackGenerator;
    private final CurrentLocaleProvider currentLocaleProvider;
    private final DefaultNotFoundConfig defaultNotFoundConfig;

    public DefaultResourcesBackend(TextBackend textBackend, FallbackGenerator fallbackGenerator, CurrentLocaleProvider currentLocaleProvider, DefaultNotFoundConfig defaultNotFoundConfig) {
        this.textBackend = textBackend;
        this.fallbackGenerator = fallbackGenerator;
        this.currentLocaleProvider = currentLocaleProvider;
        this.defaultNotFoundConfig = defaultNotFoundConfig;
    }

    @Override
    public String getText(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, NotFoundConfig notFoundConfig) {
        var maybeText = maybeText(bundleInfo, methodInfo, locale);
        if (maybeText!=null) {
            return maybeText;
        } else {
            return performNotFoundHandlingForText(bundleInfo, methodInfo, notFoundConfig, locale, NO_ARGS);
        }
    }

    @Override
    public @Nullable String maybeText(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale) {
        var localeToUse = locale(locale);
        if (localeToUse==null) {
            return null;
        }
        return this.textBackend.maybeText(bundleInfo, methodInfo, localeToUse);
    }

    @Override
    public String getFmt(BundleInfo bundleInfo, MethodInfo methodInfo, Object[] args, @Nullable Locale locale, NotFoundConfig notFoundConfig) {
        var maybeFmt = maybeFmt(bundleInfo, methodInfo, args, locale);
        if (maybeFmt!=null) {
            return maybeFmt;
        } else {
            return performNotFoundHandlingForText(bundleInfo, methodInfo, notFoundConfig, locale, args);
        }
    }

    @Override
    public @Nullable String maybeFmt(BundleInfo bundleInfo, MethodInfo methodInfo, Object[] args, @Nullable Locale locale) {
        var localeToUse = locale(locale);
        if (localeToUse==null) {
            return null;
        }
        return this.textBackend.maybeFmt(bundleInfo, methodInfo, args, localeToUse);
    }

    @Override
    public InputStream getBin(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale) {
        // TODO: Implement me
        return null;
    }

    @Override
    public @Nullable InputStream maybeBin(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale) {
        // TODO: Implement me
        return null;
    }

    private String performNotFoundHandlingForText(BundleInfo bundleInfo, MethodInfo methodInfo, NotFoundConfig notFoundConfig, @Nullable Locale locale, Object[] args) {
        var resolvedNotFoundConfig = defaultNotFoundConfig.with(notFoundConfig);
        switch (resolvedNotFoundConfig) {
            case THROW:
                return throwNotFound(bundleInfo, methodInfo, locale);
            case FALLBACK:
                return fallbackGenerator.fallbackMsgFor(bundleInfo, methodInfo, locale, args);
            default:
                throw new TyResException("Unknown not found config: " +resolvedNotFoundConfig);
        }
    }

    private <T> T throwNotFound(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale) {
        if (locale==null) {
            var debugString = WithConciseDebugString.build(List.of(bundleInfo, methodInfo));
            throw new TyResException("No locale found resolving '" + debugString + "'.");
        } else {
            var debugString = WithConciseDebugString.build(List.of(locale.toLanguageTag(), bundleInfo, methodInfo));
            throw new TyResException("Resource '" + debugString + "' not found.");
        }
    }

    @Nullable
    private Locale locale(@Nullable Locale locale) {
        if (locale!=null) {
            return locale;
        } else {
            return currentLocaleProvider.currentLocale();
        }
    }
}
