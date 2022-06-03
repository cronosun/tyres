package com.github.cronosun.tyres.core;

import org.jetbrains.annotations.Nullable;

/**
 * Implementations can enrich {@link BundleInfo} and {@link EntryInfo} with implementation specific data.
 */
public interface ImplementationDataProvider {
  @Nullable
  Object implementationDataForBundle(BundleInfo bundleInfo);

  @Nullable
  Object implementationDataForTextEntry(EntryInfo.TextEntry textEntry);

  @Nullable
  Object implementationDataForBinEntry(EntryInfo.BinEntry binEntry);
}
