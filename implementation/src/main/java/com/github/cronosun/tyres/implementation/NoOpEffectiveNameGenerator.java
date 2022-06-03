package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.Filename;
import javax.annotation.Nullable;

final class NoOpEffectiveNameGenerator implements EffectiveNameGenerator {

  private static final NoOpEffectiveNameGenerator INSTANCE = new NoOpEffectiveNameGenerator();

  private NoOpEffectiveNameGenerator() {}

  public static NoOpEffectiveNameGenerator instance() {
    return INSTANCE;
  }

  @Override
  @Nullable
  public BaseName effectiveBaseNameForText(BundleInfo bundleInfo) {
    return null;
  }

  @Override
  @Nullable
  public BaseName effectiveBaseNameForBin(BundleInfo bundleInfo) {
    return null;
  }

  @Override
  @Nullable
  public String effectiveName(EntryInfo.TextEntry entry) {
    return null;
  }

  @Override
  public String fromEffectiveNameToDeclaredName(
    BaseName declaredBaseName,
    BaseName effectiveBaseName,
    String effectiveName
  ) {
    return effectiveName;
  }

  @Override
  @Nullable
  public Filename effectiveName(EntryInfo.BinEntry entry) {
    return null;
  }
}
