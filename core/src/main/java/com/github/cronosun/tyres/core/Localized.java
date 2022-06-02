package com.github.cronosun.tyres.core;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * Contains 0-n localized messages / texts.
 */
@ThreadSafe
public final class Localized implements Resolvable {

  private static final Localized EMPTY = new Localized(Map.of());
  private final Map<Locale, String> localizations;
  private final transient Object internalSyncLock = new Object();

  @Nullable
  private transient volatile Map<Locale, Object> resultCache;

  @Nullable
  private transient volatile Map<String, String> serializedCache;

  private Localized(Map<Locale, String> localizations) {
    this.localizations = localizations;
  }

  public static Localized empty() {
    return EMPTY;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Localized fromText(Text text, Set<Locale> locales) {
    return fromText(text, NotFoundConfig.WithNullAndDefault.DEFAULT, locales);
  }

  public static Localized fromText(
    Text text,
    NotFoundConfig.WithNullAndDefault notFoundConfig,
    Set<Locale> locales
  ) {
    var builder = builder();
    for (var locale : locales) {
      var maybeText = fromTextSingleLocale(text, notFoundConfig, locale);
      if (maybeText != null) {
        builder.add(locale, maybeText);
      }
    }
    return builder.build();
  }

  @Nullable
  private static String fromTextSingleLocale(
    Text text,
    NotFoundConfig.WithNullAndDefault notFoundConfig,
    Locale locale
  ) {
    return text.getText(locale, notFoundConfig);
  }

  /**
   * Creates a new message from the serialized representation.
   *
   * @see #serialized()
   */
  public static Localized deserialize(Map<String, String> map) {
    if (map.isEmpty()) {
      return Localized.empty();
    }
    var builder = Localized.builder();
    for (var entry : map.entrySet()) {
      var languageTag = entry.getKey();
      builder.add(Locale.forLanguageTag(languageTag), entry.getValue());
    }
    return builder.build();
  }

  public Set<Locale> availableLocales() {
    return localizations.keySet();
  }

  /**
   * Get the message for given locale.
   * <p>
   * Note: Also includes candidates, as defined by
   * {@link ResourceBundle.Control#getCandidateLocales(String, Locale)}.
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Localized that = (Localized) o;
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

  /**
   * Returns the serialized representation of this message. This is useful if you want to transfer the message
   * to the client using Jackson for example.
   * <p>
   * Keys are {@link Locale#toLanguageTag()}, values are the message strings. Note: The ROOT language is
   * mapped to `und`.
   *
   * @see #deserialize(Map)
   */
  public Map<String, String> serialized() {
    var serialized = this.serializedCache;
    if (serialized == null) {
      synchronized (this.internalSyncLock) {
        serialized = this.serializedCache;
        if (serialized == null) {
          serialized = doSerialize();
          this.serializedCache = serialized;
          return serialized;
        } else {
          return serialized;
        }
      }
    } else {
      return serialized;
    }
  }

  private Map<String, String> doSerialize() {
    var inputStream = localizations.entrySet().stream();
    return inputStream
      .map(item -> new String[] { item.getKey().toLanguageTag(), item.getValue() })
      .collect(Collectors.toUnmodifiableMap(item -> item[0], item -> item[1]));
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
      synchronized (this.internalSyncLock) {
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

  @Override
  public Text resolve(Resources resources) {
    return new LocalizedText(this, resources);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(
      serialized()
        .entrySet()
        .stream()
        .map(entry -> WithConciseDebugString.association(entry.getKey(), entry.getValue()))
    );
  }

  private static final class LocalizedText implements Text {

    private final Localized localized;
    private final Resources resources;

    private LocalizedText(Localized localized, Resources resources) {
      this.localized = localized;
      this.resources = resources;
    }

    @Override
    public @Nullable ResInfo.TextResInfo resInfo() {
      return null;
    }

    @Override
    public @Nullable String getText(
      @Nullable Locale locale,
      NotFoundConfig.WithNullAndDefault notFoundConfig
    ) {
      final String message;
      Locale resolvedLocale;
      if (locale != null) {
        resolvedLocale = locale;
      } else {
        resolvedLocale = resources.currentLocale();
      }
      if (resolvedLocale != null) {
        message = localized.messageWithCandidates(resolvedLocale);
      } else {
        message = null;
      }
      if (message == null) {
        var notFoundConfigNoDefault = notFoundConfig.withNullNoDefault(
          resources.defaultNotFoundConfig()
        );
        switch (notFoundConfigNoDefault) {
          case THROW:
            if (resolvedLocale != null) {
              throw new TyResException(
                "Text for locale " + resolvedLocale + " not found in '" + localized + "'."
              );
            } else {
              throw new TyResException("No locale found to get text from " + localized + "'.");
            }
          case FALLBACK:
            if (resolvedLocale != null) {
              return WithConciseDebugString.build(
                List.of(
                  "localized_msg",
                  Objects.requireNonNullElse(locale, resolvedLocale.toLanguageTag())
                )
              );
            } else {
              return WithConciseDebugString.build(List.of("localized_msg", "missing_locale"));
            }
          case NULL:
            return null;
          default:
            throw new TyResException("Unknown not found config: " + notFoundConfigNoDefault);
        }
      } else {
        return message;
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      LocalizedText that = (LocalizedText) o;
      return localized.equals(that.localized) && resources.equals(that.resources);
    }

    @Override
    public int hashCode() {
      return Objects.hash(localized, resources);
    }

    @Override
    public String toString() {
      return "LocalizedText{" + "localized=" + localized + ", resources=" + resources + '}';
    }

    @Override
    public String conciseDebugString() {
      return localized.conciseDebugString();
    }
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

    public Localized build() {
      var takenLocalizations = this.localizations;
      this.localizations = null;
      if (takenLocalizations != null) {
        return new Localized(Collections.unmodifiableMap(takenLocalizations));
      } else {
        return Localized.empty();
      }
    }
  }

  private static final class CacheMarkerNotFound {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final CacheMarkerNotFound INSTANCE = new CacheMarkerNotFound();

    private CacheMarkerNotFound() {}
  }

  private static final class LocaleUtil {

    private static final ResourceBundle.Control RES_BUNDLE_CONTROL = ResourceBundle.Control.getControl(
      FORMAT_PROPERTIES
    );

    private LocaleUtil() {}

    public static List<Locale> getCandidateLocales(Locale locale) {
      return RES_BUNDLE_CONTROL.getCandidateLocales("", locale);
    }
  }
}
