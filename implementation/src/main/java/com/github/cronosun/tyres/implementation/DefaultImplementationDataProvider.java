package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

final class DefaultImplementationDataProvider implements ImplementationDataProvider {

  private final Once<EffectiveNameGenerator> effectiveNameGenerator;

  public DefaultImplementationDataProvider(Once<EffectiveNameGenerator> effectiveNameGenerator) {
    this.effectiveNameGenerator = effectiveNameGenerator;
  }

  @Override
  public @Nullable Object implementationDataForBundle(BundleInfo bundleInfo) {
    var effectiveNameGenerator = this.effectiveNameGenerator.get();
    var binBaseName = effectiveNameGenerator.effectiveBaseNameForBin(bundleInfo);
    var textBaseName = effectiveNameGenerator.effectiveBaseNameForText(bundleInfo);
    if (binBaseName != null || textBaseName != null) {
      var baseName = bundleInfo.baseName();
      return new EffectiveBaseNames(
        Objects.requireNonNullElse(textBaseName, baseName),
        Objects.requireNonNullElse(binBaseName, baseName)
      );
    } else {
      // not required, seems to be the no-op implementation.
      return null;
    }
  }

  @Override
  public @Nullable Object implementationDataForTextEntry(EntryInfo.TextEntry textEntry) {
    var effectiveNameGenerator = this.effectiveNameGenerator.get();
    return effectiveNameGenerator.effectiveName(textEntry);
  }

  @Override
  public @Nullable Object implementationDataForBinEntry(EntryInfo.BinEntry binEntry) {
    var effectiveNameGenerator = this.effectiveNameGenerator.get();
    return effectiveNameGenerator.effectiveName(binEntry);
  }

  public static BaseName baseNameForText(BundleInfo bundleInfo) {
    var implementationData = bundleInfo.implementationData();
    if (implementationData instanceof EffectiveBaseNames) {
      var cast = (EffectiveBaseNames) implementationData;
      return cast.textBaseName;
    } else {
      return bundleInfo.baseName();
    }
  }

  public static String name(EntryInfo.TextEntry entry) {
    var implementationData = entry.implementationData();
    if (implementationData instanceof String) {
      return (String) implementationData;
    } else {
      return entry.name();
    }
  }

  public static BaseName baseNameForBin(BundleInfo bundleInfo) {
    var implementationData = bundleInfo.implementationData();
    if (implementationData instanceof EffectiveBaseNames) {
      var cast = (EffectiveBaseNames) implementationData;
      return cast.binBaseName;
    } else {
      return bundleInfo.baseName();
    }
  }

  public static Filename filename(EntryInfo.BinEntry entry) {
    var implementationData = entry.implementationData();
    if (implementationData instanceof Filename) {
      return (Filename) implementationData;
    } else {
      return entry.filename();
    }
  }

  private static final class EffectiveBaseNames {

    private final BaseName textBaseName;
    private final BaseName binBaseName;

    private EffectiveBaseNames(BaseName textBaseName, BaseName binBaseName) {
      this.textBaseName = textBaseName;
      this.binBaseName = binBaseName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      EffectiveBaseNames that = (EffectiveBaseNames) o;
      return textBaseName.equals(that.textBaseName) && binBaseName.equals(that.binBaseName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(textBaseName, binBaseName);
    }

    @Override
    public String toString() {
      return (
        "EffectiveBaseNames{" +
        "textBaseName=" +
        textBaseName +
        ", binBaseName=" +
        binBaseName +
        '}'
      );
    }
  }
}
