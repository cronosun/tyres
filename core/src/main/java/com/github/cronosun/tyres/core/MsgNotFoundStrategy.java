package com.github.cronosun.tyres.core;

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
