package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.Filename;
import javax.annotation.Nullable;

public interface EffectiveNameGenerator {
  /**
   * Returns the empty implementation: The empty implementation does nothing: It does not rewrite names.
   */
  static EffectiveNameGenerator empty() {
    return NoOpEffectiveNameGenerator.instance();
  }

  /**
   * Use this if you want to use one single base name for text resources and/or binary resources.
   * <p>
   * Text resources will get this name: `[ORIGINAL_BASE_NAME].[ORIGINAL_NAME]`.
   * Binary resources will get this file name: `[ORIGINAL_BASE_NAME].[ORIGINAL_FILE_NAME]`.
   */
  static EffectiveNameGenerator singleBaseName(
    @Nullable BaseName textBaseName,
    @Nullable BaseName binBaseName
  ) {
    return new SingleBaseNameEffectiveNameGenerator(textBaseName, binBaseName);
  }

  @Nullable
  BaseName effectiveBaseNameForText(BundleInfo bundleInfo);

  @Nullable
  BaseName effectiveBaseNameForBin(BundleInfo bundleInfo);

  @Nullable
  String effectiveName(EntryInfo.TextEntry entry);

  /**
   * Reverse of {@link #effectiveName(EntryInfo.TextEntry)}: Returns the declared name
   * ({@link EntryInfo.TextEntry#name()}) given the effective name.
   * <p>
   * Returns <code>null</code> if the given effective name is not a name of this bundle.
   */
  @Nullable
  String fromEffectiveNameToDeclaredName(
    BaseName declaredBaseName,
    BaseName effectiveBaseName,
    String effectiveName
  );

  @Nullable
  Filename effectiveName(EntryInfo.BinEntry entry);
}
