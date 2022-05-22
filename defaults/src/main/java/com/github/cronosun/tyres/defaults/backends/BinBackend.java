package com.github.cronosun.tyres.defaults.backends;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ResInfoDetails;
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
   *
   * Note: Throws resource is not {@link ResInfoDetails.Kind#BINARY}.
   */
  @Nullable
  InputStream maybeBin(ResInfo resInfo, Locale locale);

  /**
   * Returns <code>true</code> if this resource exists. Note: This method should only be used for validation,
   * it might be as slow as calling {@link #maybeBin(ResInfo, Locale)}.
   */
  boolean validateExists(ResInfo resInfo, Locale locale);
}
