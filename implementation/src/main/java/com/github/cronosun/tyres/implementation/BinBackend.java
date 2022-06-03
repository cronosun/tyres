package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.Filename;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface BinBackend {
  /**
   * Get the binary if found or <code>null</code> if there's no such binary.
   */
  @Nullable
  InputStream maybeBin(
    EntryInfo.BinEntry entry,
    BaseName baseName,
    Filename filename,
    Locale locale
  );

  static BinBackend resourceBundleInstance() {
    return ResourceBundleBinBackend.instance();
  }

  /**
   * Validates the resource (asserts it exists and can be read). Throws a
   * {@link com.github.cronosun.tyres.core.TyResException} if there's a validation error.
   * <p>
   * Note: This is an optional operation. If the implementation does not support validation, this method
   * does nothing.
   */
  void validate(EntryInfo.BinEntry entry, BaseName baseName, Filename filename, Locale locale);
}
