package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import java.io.InputStream;
import java.util.Locale;

import com.github.cronosun.tyres.core.experiment.ResInfo;
import org.jetbrains.annotations.Nullable;

public interface ResourcesBackend {
  @Nullable
  String getText(
    ResInfo.Text info,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  String getFmt(
          ResInfo.Text info,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  InputStream getInputStream(ResInfo.Bin info, @Nullable Locale locale, boolean required);
}
