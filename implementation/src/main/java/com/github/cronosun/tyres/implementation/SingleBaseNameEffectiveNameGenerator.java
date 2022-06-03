package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.Filename;
import org.jetbrains.annotations.Nullable;

public class SingleBaseNameEffectiveNameGenerator implements EffectiveNameGenerator {

  @Nullable
  private final BaseName textBaseName;

  @Nullable
  private final BaseName binBaseName;

  public SingleBaseNameEffectiveNameGenerator(
    @Nullable BaseName textBaseName,
    @Nullable BaseName binBaseName
  ) {
    this.textBaseName = textBaseName;
    this.binBaseName = binBaseName;
  }

  @Override
  public @Nullable BaseName effectiveBaseNameForText(BundleInfo bundleInfo) {
    return textBaseName;
  }

  @Override
  public @Nullable BaseName effectiveBaseNameForBin(BundleInfo bundleInfo) {
    return binBaseName;
  }

  @Override
  public @Nullable String effectiveName(EntryInfo.TextEntry entry) {
    var textBaseName = this.textBaseName;
    if (textBaseName != null) {
      // add the base name in front
      var prefix = entry.bundleInfo().baseName().value();
      return prefix + BaseName.separator() + entry.name();
    } else {
      return null;
    }
  }

  @Override
  public @Nullable String fromEffectiveNameToDeclaredName(
    BaseName declaredBaseName,
    BaseName effectiveBaseName,
    String effectiveName
  ) {
    var textBaseName = this.textBaseName;
    if (textBaseName != null) {
      var prefix = declaredBaseName.value() + BaseName.separator();
      if (effectiveName.startsWith(prefix)) {
        var prefixLen = prefix.length();
        return effectiveName.substring(prefixLen);
      } else {
        return null;
      }
    } else {
      return effectiveName;
    }
  }

  @Override
  public @Nullable Filename effectiveName(EntryInfo.BinEntry entry) {
    var binBaseName = this.binBaseName;
    if (binBaseName != null) {
      var declaredFileName = entry.filename();
      var prefix = entry.bundleInfo().baseName().value();
      var newFileName = prefix + BaseName.separator() + declaredFileName.value();
      return Filename.from(newFileName);
    } else {
      return null;
    }
  }
}
