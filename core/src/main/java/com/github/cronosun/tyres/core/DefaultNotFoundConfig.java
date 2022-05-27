package com.github.cronosun.tyres.core;

/**
 * Depending on the situation you might want to configure the application's "not found strategy" differently. For
 * Unit-Tests (and maybe also local development) you might want to throw exceptions to detect missing or invalid
 * resources, for production builds you might want to use fallbacks.
 */
public enum DefaultNotFoundConfig {
  /**
   * If there's no such message, throws a {@link TyResException} exception.
   */
  THROW(NotFoundConfig.WithNullNoDefault.THROW),
  /**
   * If there's no such message, returns the fallback value.
   */
  FALLBACK(NotFoundConfig.WithNullNoDefault.FALLBACK);

  private final NotFoundConfig.WithNullNoDefault withNull;

  DefaultNotFoundConfig(NotFoundConfig.WithNullNoDefault withNull) {
    this.withNull = withNull;
  }

  public NotFoundConfig.WithNullNoDefault withNull() {
    return withNull;
  }
}
