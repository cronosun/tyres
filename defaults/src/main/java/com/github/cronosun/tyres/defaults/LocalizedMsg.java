package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * Contains 0-n localized messages.
 */
@ThreadSafe
public final class LocalizedMsg implements Msg {

  private static final LocalizedMsg EMPTY = new LocalizedMsg(Map.of());

  public static LocalizedMsg empty() {
    return EMPTY;
  }

  private final Map<Locale, String> localizations;

  @Nullable
  private transient volatile Map<Locale, Object> parentCache;

  private LocalizedMsg(Map<Locale, String> localizations) {
    this.localizations = localizations;
  }

  @Nullable
  public String message(Locale locale, boolean includeParent) {
    var msg = localizations.get(locale);
    if (msg != null) {
      return msg;
    }
    if (includeParent) {
      return messageFromParentLocaleWithCache(locale);
    } else {
      return null;
    }
  }

  public Set<Locale> availableLocales() {
    return localizations.keySet();
  }

  @Override
  public String msg(
    Resources resources,
    Resources.NotFoundStrategy notFoundStrategy,
    Locale locale
  ) {
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
  public String maybeMsg(Resources resources, Locale locale) {
    return message(locale, true);
  }

  public static Builder builder() {
    return new Builder();
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

  public static final class Builder {

    private Builder() {}

    @Nullable
    private Map<Locale, String> localizations;

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

  @Nullable
  private String messageFromParentLocaleWithCache(Locale locale) {
    // note: it can happen that the parent is computed multiple times (if accessed from multiple
    // threads, but we accept this and don't need synchronization here).
    var parentCache = this.parentCache;
    if (parentCache != null) {
      var valueFromCache = parentCache.get(locale);
      if (valueFromCache != null) {
        if (valueFromCache instanceof String) {
          return (String) valueFromCache;
        } else if (valueFromCache == CacheMarkerNotFound.INSTANCE) {
          return null;
        } else {
          throw new TyResException("Unvalid / unknown value in cache: " + valueFromCache);
        }
      }
    }

    // ok, nothing in cache, we compute it
    if (parentCache == null) {
      synchronized (this) {
        parentCache = this.parentCache;
        if (parentCache == null) {
          parentCache = new ConcurrentHashMap<>();
          this.parentCache = parentCache;
        }
      }
    }

    var computedValue = messageFromParentLocaleNoCache(locale);
    parentCache.put(
      locale,
      Objects.requireNonNullElse(computedValue, CacheMarkerNotFound.INSTANCE)
    );
    return computedValue;
  }

  @Nullable
  private String messageFromParentLocaleNoCache(Locale locale) {
    Locale currentLocale = locale;
    while (true) {
      currentLocale = LocaleUtil.getParent(currentLocale);
      if (currentLocale == null) {
        return null;
      }
      var message = this.localizations.get(currentLocale);
      if (message != null) {
        return message;
      }
    }
  }

  private static final class CacheMarkerNotFound {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final CacheMarkerNotFound INSTANCE = new CacheMarkerNotFound();

    private CacheMarkerNotFound() {}
  }
}
