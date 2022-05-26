package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.ResInfo;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface BinBackend {
  /**
   * Maybe get the binary.
   */
  @Nullable
  InputStream maybeBin(ResInfo.Bin info, Locale locale);

  static BinBackend resourceBundleInstance() {
    return ResourceBundleBinBackend.instance();
  }
}
