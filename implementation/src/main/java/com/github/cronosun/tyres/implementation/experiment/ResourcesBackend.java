package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.MethodInfo;
import com.github.cronosun.tyres.core.experiment.NotFoundConfig;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface ResourcesBackend {
  String getText(
    BundleInfo bundleInfo,
    MethodInfo methodInfo,
    @Nullable Locale locale,
    NotFoundConfig notFoundConfig
  );

  @Nullable
  String maybeText(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale);

  String getFmt(
    BundleInfo bundleInfo,
    MethodInfo methodInfo,
    Object[] args,
    @Nullable Locale locale,
    NotFoundConfig notFoundConfig
  );

  @Nullable
  String maybeFmt(
    BundleInfo bundleInfo,
    MethodInfo methodInfo,
    Object[] args,
    @Nullable Locale locale
  );

  InputStream getBin(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale);

  @Nullable
  InputStream maybeBin(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale);
}
