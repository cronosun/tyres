package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import com.github.cronosun.tyres.core.experiment.Resources2;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public interface ResourcesBackend {
  @Nullable
  String getText(
    Resources2 resources2,
    ResInfo.TextResInfo info,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  String getFmt(
    Resources2 resources2,
    ResInfo.TextResInfo info,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  InputStream getInputStream(
    Resources2 resources2,
    ResInfo.BinResInfo info,
    @Nullable Locale locale,
    boolean required
  );

  void validateAllResoucesFromBundle(Supplier<Stream<ResInfo>> resInfo, Locale locale);
}
