package com.github.cronosun.tyres.implementation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

final class CachedMessageFormatter extends AbstractMessageFormatter {

    private final ConcurrentHashMap<String, ConcurrentHashMap<Locale, MessageFormat>> cache = new ConcurrentHashMap<>();

    @Override
    protected String formatInternal(String pattern, Object[] args, Locale locale) {
        var messageFormat = getMessageFormatFromCacheOrCreate(pattern, locale);
        // https://youtrack.jetbrains.com/issue/IDEA-116610
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (messageFormat) {
            return messageFormat.format(args);
        }
    }

    @Override
    protected int validateAndReturnNumberOfArguments(String pattern, Locale locale) {
        var messageFormat = getMessageFormatFromCacheOrCreate(pattern, locale);
        // https://youtrack.jetbrains.com/issue/IDEA-116610
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (messageFormat) {
            return messageFormat.getFormats().length;
        }
    }

    private MessageFormat getMessageFormatFromCacheOrCreate(String pattern, Locale locale) {
        var localeMessageFormatMap = getOrCreateLocaleMessageFormatMap(pattern);
        var messageFormat = localeMessageFormatMap.get(locale);
        if (messageFormat!=null) {
            // 98% case
            return messageFormat;
        } else {
            // https://youtrack.jetbrains.com/issue/IDEA-116610
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (localeMessageFormatMap) {
                messageFormat = localeMessageFormatMap.get(locale);
                if (messageFormat!=null) {
                    return messageFormat;
                } else {
                    messageFormat = new MessageFormat(pattern, locale);
                    localeMessageFormatMap.put(locale, messageFormat);
                    return messageFormat;
                }
            }
        }
    }

    private ConcurrentHashMap<Locale, MessageFormat> getOrCreateLocaleMessageFormatMap(String pattern) {
        var localeMessageFormatMap = this.cache.get(pattern);
        if (localeMessageFormatMap !=null) {
            // 98% case
            return localeMessageFormatMap;
        } else {
            synchronized (this.cache) {
                localeMessageFormatMap = this.cache.get(pattern);
                if (localeMessageFormatMap!=null) {
                    // 1% case
                    return localeMessageFormatMap;
                } else {
                    // 1% case
                    localeMessageFormatMap = new ConcurrentHashMap<>();
                    this.cache.put(pattern, localeMessageFormatMap);
                    return localeMessageFormatMap;
                }
            }
        }
    }
}
