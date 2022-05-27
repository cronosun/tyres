package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

final class CachedConfigurableValidator implements ValidatorBackend {

  private final Supplier<ValidatorBackend> validatorBackend;
  private final boolean validateOnBundleUse;
  private final Map<CacheKey, CacheResult> cachedResults = new ConcurrentHashMap<>();
  private final Object lock = new Object();

  public CachedConfigurableValidator(
    Supplier<ValidatorBackend> validatorBackend,
    boolean validateOnBundleUse
  ) {
    this.validatorBackend = validatorBackend;
    this.validateOnBundleUse = validateOnBundleUse;
  }

  @Override
  public void validate(When when, Class<?> bundleClass, Locale locale) {
    if (when == When.ON_USE && !this.validateOnBundleUse) {
      return;
    }
    validateInternal(bundleClass, locale);
  }

  private void validateInternal(Class<?> bundleClass, Locale locale) {
    var key = new CacheKey(bundleClass, locale);
    var result = this.cachedResults.get(key);
    if (result == null) {
      synchronized (this.lock) {
        result = this.cachedResults.get(key);
        if (result == null) {
          result = validateUsingBackend(bundleClass, locale);
          this.cachedResults.put(key, result);
          executeCacheResult(result);
        } else {
          executeCacheResult(result);
        }
      }
    } else {
      executeCacheResult(result);
    }
  }

  private CacheResult validateUsingBackend(Class<?> bundleClass, Locale locale) {
    try {
      this.validatorBackend.get().validate(When.OTHER, bundleClass, locale);
    } catch (Exception exception) {
      if (exception instanceof TyResException) {
        var cast = (TyResException) exception;
        return new CacheResult(cast);
      } else {
        return new CacheResult(new TyResException("Validating threw an exception", exception));
      }
    }
    return CacheResult.NO_ERROR;
  }

  private void executeCacheResult(CacheResult cacheResult) {
    var exception = cacheResult.exception;
    if (exception != null) {
      throw exception;
    }
  }

  private static final class CacheResult {

    private static final CacheResult NO_ERROR = new CacheResult(null);

    @Nullable
    private final TyResException exception;

    private CacheResult(@Nullable TyResException exception) {
      this.exception = exception;
    }
  }

  private static final class CacheKey {

    private final Class<?> bundleClass;
    private final Locale locale;

    private CacheKey(Class<?> bundleClass, Locale locale) {
      this.bundleClass = bundleClass;
      this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CacheKey cacheKey = (CacheKey) o;
      return bundleClass.equals(cacheKey.bundleClass) && locale.equals(cacheKey.locale);
    }

    @Override
    public int hashCode() {
      return Objects.hash(bundleClass, locale);
    }
  }
}
