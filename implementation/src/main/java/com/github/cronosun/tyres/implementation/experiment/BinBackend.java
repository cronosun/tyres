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
  InputStream maybeBin(ResInfo.BinResInfo info, Locale locale);

  static BinBackend resourceBundleInstance() {
    return ResourceBundleBinBackend.instance();
  }

  /**
   * Validates the resource. Throws a {@link com.github.cronosun.tyres.core.TyResException} if there's
   * a validation error.
   * <p>
   * Note: This is an optional operation. If the implementation does not support validation, this method
   * does nothing.
   */
  void validate(ResInfo.BinResInfo info, Locale locale);
}
