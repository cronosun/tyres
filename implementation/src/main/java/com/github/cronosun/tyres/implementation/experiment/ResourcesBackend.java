package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.NotFoundConfig;
import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.Resources;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public interface ResourcesBackend {
  @Nullable
  String getText(
    Resources resources,
    ResInfo.TextResInfo info,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  String getFmt(
    Resources resources,
    ResInfo.TextResInfo info,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  InputStream getInputStream(
    Resources resources,
    ResInfo.BinResInfo info,
    @Nullable Locale locale,
    boolean required
  );

  void validateAllResourcesFromBundle(Supplier<Stream<ResInfo>> resInfo, Locale locale);
}
