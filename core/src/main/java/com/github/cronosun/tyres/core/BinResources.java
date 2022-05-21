package com.github.cronosun.tyres.core;

import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface BinResources {
  /**
   * Returns the binary as input stream or <code>null</code> if resource cannot be found.
   */
  @Nullable
  InputStream maybeBin(BinRes resource, Locale locale);

  InputStream bin(BinRes resource, Locale locale);
}
