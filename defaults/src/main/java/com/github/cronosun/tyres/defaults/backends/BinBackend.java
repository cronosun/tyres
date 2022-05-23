package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.ResInfo;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface BinBackend {
  /**
   * Returns the default backend that reads the binary files from resources.
   */
  static BinBackend backendUsingResources() {
    return DefaultBinBackend.instance();
  }

  /**
   * Maybe get the binary.
   */
  @Nullable
  InputStream maybeBin(ResInfo.Bin resInfo, Locale locale);

  /**
   * Returns <code>true</code> if this resource exists. Note: This method should only be used for validation,
   * it might be as slow as calling {@link #maybeBin(ResInfo.Bin, Locale)}.
   */
  boolean validateExists(ResInfo.Bin resInfo, Locale locale);
}
