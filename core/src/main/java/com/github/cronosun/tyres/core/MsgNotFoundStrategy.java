package com.github.cronosun.tyres.core;

/**
 * Depending on the situation you might want to configure the application's "not found strategy" differently. For
 * Unit-Tests (and maybe also local development) you might want to throw exceptions to detect missing or invalid
 * resources, for production builds you might want to use fallbacks.
 */
public enum MsgNotFoundStrategy {
  /**
   * If there's no such message, throws a {@link TyResException} exception.
   */
  THROW,
  /**
   * If there's no such message, returns the fallback value.
   */
  FALLBACK,
}
