package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

// TODO: Serialize, de-serialize
/**
 * Contains 0-n localized messages.
 */
@ThreadSafe
public final class LocalizedMsg implements Msg {

  private static final LocalizedMsg EMPTY = new LocalizedMsg(Map.of());
  private final Map<Locale, String> localizations;
  private final transient Object resultCacheLock = new Object();

  @Nullable
  private transient volatile Map<Locale, Object> resultCache;

  private LocalizedMsg(Map<Locale, String> localizations) {
    this.localizations = localizations;
  }

  public static LocalizedMsg empty() {
    return EMPTY;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Set<Locale> availableLocales() {
    return localizations.keySet();
  }

  /**
   * Get the message for given locale.
   * <p>
   * Note: Also includes candidates, as defined by
   * {@link java.util.ResourceBundle.Control#getCandidateLocales(String, Locale)}.
   */
  @Nullable
  public String message(Locale locale) {
    return messageWithCandidates(locale);
  }

  /**
   * Get the message for given locale.
   * <p>
   * Note: Unlike {@link #message(Locale)}, this only results a result if the locale matches exactly.
   */
  @Nullable
  public String messageExact(Locale locale) {
    return this.localizations.get(locale);
  }

  @Override
  public String msg(MsgResources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var message = maybeMsg(resources, locale);
    if (message == null) {
      switch (notFoundStrategy) {
        case THROW:
          throw new TyResException("Text for locale " + locale + " not found in '" + this + "'.");
        case FALLBACK:
          return "{localized_msg:" + locale.toLanguageTag() + "}";
        default:
          throw new TyResException("Unknown not found strategy: " + notFoundStrategy);
      }
    } else {
      return message;
    }
  }

  @Nullable
  @Override
  public String maybeMsg(MsgResources resources, Locale locale) {
    return messageWithCandidates(locale);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LocalizedMsg that = (LocalizedMsg) o;
    return localizations.equals(that.localizations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localizations);
  }

  @Override
  public String toString() {
    return "LocalizedMsg{" + localizations + '}';
  }

  @Nullable
  private String messageWithCandidates(Locale locale) {
    var localizations = this.localizations;
    // fast path
    var directResult = localizations.get(locale);
    if (directResult != null) {
      return directResult;
    }
    if (localizations.isEmpty()) {
      return null;
    }

    // try from cache
    var cache = this.resultCache;
    if (cache != null) {
      var resultFromCache = cache.get(locale);
      if (resultFromCache instanceof String) {
        return (String) resultFromCache;
      } else if (CacheMarkerNotFound.INSTANCE == resultFromCache) {
        // never have a result
        return null;
      }
    }

    var computedResult = getMessageWithCandidatesNotIncludingGivenLocale(locale);

    // add to cache (note: it might happen that the result that we just computed has been computed in another
    // thread too: I think it's better to compute a result twice from time to tome instead of holding a lock for
    // too long).
    if (cache != null) {
      addToCache(cache, locale, computedResult);
    } else {
      synchronized (this.resultCacheLock) {
        cache = this.resultCache;
        if (cache == null) {
          cache = new ConcurrentHashMap<>();
          this.resultCache = cache;
        }
      }
      addToCache(cache, locale, computedResult);
    }

    return computedResult;
  }

  private void addToCache(Map<Locale, Object> cache, Locale locale, @Nullable String result) {
    cache.put(locale, Objects.requireNonNullElse(result, CacheMarkerNotFound.INSTANCE));
  }

  @Nullable
  private String getMessageWithCandidatesNotIncludingGivenLocale(Locale locale) {
    var candidateLocales = LocaleUtil.getCandidateLocales(locale);
    for (var candidateLocale : candidateLocales) {
      if (candidateLocale.equals(locale)) {
        continue;
      }
      var maybeResult = this.localizations.get(candidateLocale);
      if (maybeResult != null) {
        return maybeResult;
      }
    }
    return null;
  }

  public static final class Builder {

    @Nullable
    private Map<Locale, String> localizations;

    private Builder() {}

    public void add(Locale locale, String message) {
      var localizations = this.localizations;
      if (localizations == null) {
        localizations = new HashMap<>();
        this.localizations = localizations;
      }
      localizations.put(locale, message);
    }

    public Builder with(Locale locale, String message) {
      add(locale, message);
      return this;
    }

    public LocalizedMsg build() {
      var takenLocalizations = this.localizations;
      this.localizations = null;
      if (takenLocalizations != null) {
        return new LocalizedMsg(Collections.unmodifiableMap(takenLocalizations));
      } else {
        return LocalizedMsg.empty();
      }
    }
  }

  private static final class CacheMarkerNotFound {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final CacheMarkerNotFound INSTANCE = new CacheMarkerNotFound();

    private CacheMarkerNotFound() {}
  }
}
