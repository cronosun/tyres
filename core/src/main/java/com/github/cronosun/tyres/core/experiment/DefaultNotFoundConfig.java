package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.TyResException;

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
  // TODO
  /*public DefaultNotFoundConfig with(NotFoundConfig notFoundConfig) {
    switch (notFoundConfig) {
      case THROW:
        return DefaultNotFoundConfig.THROW;
      case FALLBACK:
        return DefaultNotFoundConfig.FALLBACK;
      case DEFAULT:
        return this;
      default:
        throw new IllegalArgumentException("Unknown not found config: " + this);
    }
  }*/
}
