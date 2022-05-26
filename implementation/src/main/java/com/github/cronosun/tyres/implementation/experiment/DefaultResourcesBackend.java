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
    private final BinBackend binBackend;
    private final FallbackGenerator fallbackGenerator;
    private final CurrentLocaleProvider currentLocaleProvider;
    private final DefaultNotFoundConfig defaultNotFoundConfig;

    public DefaultResourcesBackend(TextBackend textBackend, BinBackend binBackend, FallbackGenerator fallbackGenerator, CurrentLocaleProvider currentLocaleProvider, DefaultNotFoundConfig defaultNotFoundConfig) {
        this.textBackend = textBackend;
        this.binBackend = binBackend;
        this.fallbackGenerator = fallbackGenerator;
        this.currentLocaleProvider = currentLocaleProvider;
        this.defaultNotFoundConfig = defaultNotFoundConfig;
    }

    @Override
    public @Nullable String getText(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, NotFoundConfig.WithNullAndDefault notFoundConfig) {
        var localeToUse = locale(locale);
        if (localeToUse==null) {
            return handleReturnForText(null, bundleInfo, methodInfo, null, NO_ARGS, notFoundConfig);
        }
        var value =  this.textBackend.maybeText(bundleInfo, methodInfo, localeToUse);
        return handleReturnForText(value, bundleInfo, methodInfo, localeToUse, NO_ARGS, notFoundConfig);
    }

    @Override
    public @Nullable String getFmt(BundleInfo bundleInfo, MethodInfo methodInfo, Object[] args, @Nullable Locale locale, NotFoundConfig.WithNullAndDefault notFoundConfig) {
        var localeToUse = locale(locale);
        if (localeToUse==null) {
            return handleReturnForText(null, bundleInfo, methodInfo, null, args, notFoundConfig);
        }
        var value =  this.textBackend.maybeFmt(bundleInfo, methodInfo, args, localeToUse);
        return handleReturnForText(value, bundleInfo, methodInfo, localeToUse, args, notFoundConfig);
    }

    @Override
    public @Nullable InputStream getInputStream(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, boolean required) {
        var localeToUse = locale(locale);
        final InputStream inputStream;
        if (localeToUse==null) {
            inputStream = null;
        } else {
            inputStream = this.binBackend.maybeBin(bundleInfo, methodInfo,localeToUse );
        }
        if (inputStream==null && required) {
            return throwNotFound(bundleInfo, methodInfo, localeToUse);
        } else {
            return inputStream;
        }
    }

    private String handleReturnForText(@Nullable String text, BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, Object[] args, NotFoundConfig.WithNullAndDefault notFoundConfig) {
        final DefaultNotFoundConfig defaultNotFoundConfig;
        switch (notFoundConfig) {
            case THROW:
                defaultNotFoundConfig = DefaultNotFoundConfig.THROW;
                break;
            case FALLBACK:
                defaultNotFoundConfig = DefaultNotFoundConfig.FALLBACK;
                break;
            case DEFAULT:
                defaultNotFoundConfig = this.defaultNotFoundConfig;
                break;
            case NULL:
                // no check required
                return text;
            default:
                throw new IllegalArgumentException("Unknown not found config: " + notFoundConfig);
        }
        switch (defaultNotFoundConfig) {
            case THROW:
                return throwNotFound(bundleInfo, methodInfo, locale);
            case FALLBACK:
                return fallbackGenerator.fallbackMsgFor(bundleInfo, methodInfo, locale, args);
            default:
                throw new TyResException("Unknown not found config: " +defaultNotFoundConfig);
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
