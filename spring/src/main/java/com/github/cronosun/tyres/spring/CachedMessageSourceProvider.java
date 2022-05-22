package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ThreadSafe;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
final class CachedMessageSourceProvider implements MessageSourceProvider {
    private final MessageSourceFactory factory;
    private final Map<Object, MessageSourceWithResourceNames> cache = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public CachedMessageSourceProvider(MessageSourceFactory factory) {
        this.factory = factory;
    }

    @Override
    public MessageSourceWithResourceNames messageSource(BundleInfo bundleInfo, Locale locale) {
        var cacheKey = factory.cacheKeyFor(bundleInfo, locale);
        var fromCache = this.cache.get(cacheKey);
        if (fromCache != null) {
            return fromCache;
        } else {
            synchronized (this.lock) {
                fromCache = this.cache.get(cacheKey);
                if (fromCache != null) {
                    return fromCache;
                } else {
                    // ok, generate it
                    var createdSource = this.factory.createMessageSource(bundleInfo, locale);
                    this.cache.put(createdSource.cacheKey(), createdSource.messageSource());
                    return createdSource.messageSource();
                }

            }
        }
    }
}
