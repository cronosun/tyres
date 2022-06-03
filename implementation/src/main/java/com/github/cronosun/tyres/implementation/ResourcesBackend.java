package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.EntryInfo;
import com.github.cronosun.tyres.core.NotFoundConfig;
import com.github.cronosun.tyres.core.Resources;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface ResourcesBackend {
  @Nullable
  String getText(
    Resources resources,
    EntryInfo.TextEntry entry,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  String getFmt(
    Resources resources,
    EntryInfo.TextEntry entry,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  InputStream getInputStream(
    Resources resources,
    EntryInfo.BinEntry entry,
    @Nullable Locale locale,
    boolean required
  );

  void validateAllResourcesFromBundle(Supplier<Stream<EntryInfo>> resInfo, Locale locale);
}
