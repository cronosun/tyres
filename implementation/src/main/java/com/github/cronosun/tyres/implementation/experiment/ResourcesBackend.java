package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.MethodInfo;
import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface ResourcesBackend {
  @Nullable
  String getText(
    BundleInfo bundleInfo,
    MethodInfo methodInfo,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  String getFmt(
    BundleInfo bundleInfo,
    MethodInfo methodInfo,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig.WithNullAndDefault notFoundConfig
  );

  @Nullable
  InputStream getInputStream(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, boolean required);
}
