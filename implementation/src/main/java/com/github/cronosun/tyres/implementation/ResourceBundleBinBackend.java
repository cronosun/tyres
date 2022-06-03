package com.github.cronosun.tyres.implementation;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

import com.github.cronosun.tyres.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

final class ResourceBundleBinBackend implements BinBackend {

  private static final Object NOT_FOUND = new Object();
  private static final ResourceBundleBinBackend INSTANCE = new ResourceBundleBinBackend(
    ResourceBundle.Control.getControl(FORMAT_PROPERTIES)
  );
  private final ResourceBundle.Control control;
  private final Map<CacheKey, Object> notFoundCache = new ConcurrentHashMap<>();
  private final Map<CacheKey, CacheKey> foundResults = new ConcurrentHashMap<>();

  private ResourceBundleBinBackend(ResourceBundle.Control control) {
    this.control = control;
  }

  public static ResourceBundleBinBackend instance() {
    return INSTANCE;
  }

  @Override
  public @Nullable InputStream maybeBin(
    EntryInfo.BinEntry entry,
    BaseName baseName,
    Filename filename,
    Locale locale
  ) {
    return maybeGet(entry, baseName, filename, locale);
  }

  @Override
  public void validate(
    EntryInfo.BinEntry entry,
    BaseName baseName,
    Filename filename,
    Locale locale
  ) {
    try (var inputStream = maybeBin(entry, baseName, filename, locale)) {
      if (inputStream == null && entry.required()) {
        throw new TyResException(
          "Binary resource " +
          entry.conciseDebugString() +
          " for locale '" +
          locale.toLanguageTag() +
          "' not found. If this resource is optional, see the @" +
          Validation.class.getSimpleName() +
          " annotation."
        );
      }
    } catch (IOException e) {
      throw new TyResException("Unable to load binary " + entry.conciseDebugString() + ".", e);
    }
  }

  @Nullable
  private InputStream maybeGet(
    EntryInfo.BinEntry entry,
    BaseName baseName,
    Filename filename,
    Locale locale
  ) {
    var bundleClass = entry.bundleInfo().bundleClass();

    // first try from cache
    var originalCacheKey = new CacheKey(baseName, filename, locale, bundleClass);
    var maybeFromCache = this.foundResults.get(originalCacheKey);
    if (maybeFromCache != null) {
      return maybeGetForSingleLocale(maybeFromCache);
    }

    // ok, nothing found in cache
    var candidates = control.getCandidateLocales(baseName.value(), locale);
    for (var candidate : candidates) {
      var cacheKey = new CacheKey(baseName, filename, candidate, bundleClass);
      var maybeInputStream = maybeGetForSingleLocale(cacheKey);
      if (maybeInputStream != null) {
        // add result to cache
        this.foundResults.put(originalCacheKey, cacheKey);
        return maybeInputStream;
      }
    }
    return null;
  }

  @Nullable
  private InputStream maybeGetForSingleLocale(CacheKey cacheKey) {
    if (notFoundCache.containsKey(cacheKey)) {
      return null;
    }
    var classLoader = cacheKey.bundleClass.getClassLoader();
    var baseName = cacheKey.baseName;
    var filename = cacheKey.fileName;
    var locale = cacheKey.locale;
    var resourceName = createResourceName(baseName, filename, locale);
    var inputStream = classLoader.getResourceAsStream(resourceName);
    if (inputStream == null) {
      notFoundCache.put(cacheKey, NOT_FOUND);
    }
    return inputStream;
  }

  private String createResourceName(BaseName baseName, Filename filename, Locale locale) {
    var builder = new StringBuilder();
    var resourceName = baseName.value().replace('.', '/');
    builder.append(resourceName);
    if (!resourceName.isEmpty() && !resourceName.endsWith("/")) {
      builder.append("/");
    }

    builder.append(filename.base());
    var fileLocaleSuffix = control.toBundleName("", locale);
    builder.append(fileLocaleSuffix);
    var extension = filename.extension();
    if (extension != null) {
      builder.append('.');
      builder.append(filename.extension());
    }

    return builder.toString();
  }

  private static final class CacheKey {

    private final BaseName baseName;
    private final Filename fileName;
    private final Locale locale;
    private final Class<?> bundleClass;

    private CacheKey(BaseName baseName, Filename fileName, Locale locale, Class<?> bundleClass) {
      this.baseName = baseName;
      this.fileName = fileName;
      this.locale = locale;
      this.bundleClass = bundleClass;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CacheKey cacheKey = (CacheKey) o;
      return (
        baseName.equals(cacheKey.baseName) &&
        fileName.equals(cacheKey.fileName) &&
        locale.equals(cacheKey.locale) &&
        bundleClass.equals(cacheKey.bundleClass)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(baseName, fileName, locale, bundleClass);
    }
  }
}
