package com.github.cronosun.tyres.implementation.backends;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.Filename;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.TyResException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

final class DefaultBinBackend implements BinBackend {

  private static final Object NOT_FOUND = new Object();
  private static final DefaultBinBackend INSTANCE = new DefaultBinBackend(
    ResourceBundle.Control.getControl(FORMAT_PROPERTIES)
  );
  private final ResourceBundle.Control control;
  private final Map<CacheKey, Object> notFoundCache = new ConcurrentHashMap<>();
  private final Map<CacheKey, CacheKey> foundResults = new ConcurrentHashMap<>();

  private DefaultBinBackend(ResourceBundle.Control control) {
    this.control = control;
  }

  public static DefaultBinBackend instance() {
    return INSTANCE;
  }

  @Override
  public InputStream maybeBin(ResInfo.Bin resInfo, Locale locale) {
    var filename = resInfo.filename();
    return maybeGet(resInfo, filename, locale);
  }

  @Override
  public boolean validateExists(ResInfo.Bin resInfo, Locale locale) {
    try (var maybeBin = maybeBin(resInfo, locale)) {
      return maybeBin != null;
    } catch (IOException e) {
      throw new TyResException("Unable to close resource file (this should not happen)", e);
    }
  }

  @Nullable
  private InputStream maybeGet(ResInfo resInfo, Filename filename, Locale locale) {
    var bundleInfo = resInfo.bundle();
    var baseName = bundleInfo.baseName();
    var bundleClass = bundleInfo.bundleClass();

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
    var builder = new StringBuilder(baseName.value());
    if (builder.length() != 0) {
      builder.append(".");
    }
    builder.append(filename.base());
    var baseNameString = builder.toString();

    var bundleName = control.toBundleName(baseNameString, locale);
    var suffix = Objects.requireNonNullElse(filename.extension(), "");
    return control.toResourceName(bundleName, suffix);
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
