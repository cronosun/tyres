package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

/**
 * Something that returns either of the two (never both, never none):
 *
 *  - {@link #resource()}
 *  - {@link #resolvable()}
 */
public interface Resolvable<T> {
  @Nullable
  Res<T> resource();

  @Nullable
  T resolvable();
}
